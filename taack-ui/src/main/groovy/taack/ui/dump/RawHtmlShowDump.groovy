package taack.ui.dump

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.render.TaackUiEnablerService
import taack.render.TaackUiService
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.show.IUiShowVisitor
import taack.ui.dsl.show.ShowLayout

@CompileStatic
final class RawHtmlShowDump implements IUiShowVisitor {

    final private ByteArrayOutputStream out
    final private Parameter parameter

    final String blockId
    private ShowLayout layout = ShowLayout.LIST
    private boolean fieldListOpen

    RawHtmlShowDump(final String id, final ByteArrayOutputStream out, final Parameter parameter) {
        this.out = out
        this.parameter = parameter
        this.blockId = id
    }

    @Override
    void visitShow(final ShowLayout layout) {
        this.layout = layout ?: ShowLayout.LIST
        writeShowWrapperStart()
    }

    private void writeShowWrapperStart() {
        String gridClass = layout == ShowLayout.GRID ? ' taackShowGrid' : ''
        out << "<div class='property-list taackShow col-12${gridClass}'>"
    }

    private void openFieldList() {
        if (!fieldListOpen) {
            out << "<dl class='taackShowFields'>"
            fieldListOpen = true
        }
    }

    private void closeFieldList() {
        if (fieldListOpen) {
            out << '</dl>'
            fieldListOpen = false
        }
    }

    @Override
    void visitShowEnd() {
        closeFieldList()
        out << '</div>'
    }

    @Override
    void visitSection(String i18n) {
        closeFieldList()
        out << "<div class='taackShowSection'>"
    }

    @Override
    void visitSectionEnd() {
        closeFieldList()
        out << '</div>'
    }

    @Override
    void visitShowFieldUnLabeled(Style style, FieldInfo... fields) {
        visitShowField(null, fields.last(), style)
    }

    @Override
    void visitShowFieldLabeled(Style style, FieldInfo... fields) {
        visitShowField(parameter.trField(fields), fields.last(), style)
    }

    @Override
    void visitShowFieldUnLabeled(Style style, GetMethodReturn methodReturn) {
        visitShowField(null, methodReturn.value?.toString(), style)
    }

    @Override
    void visitShowFieldLabeled(Style style, GetMethodReturn methodReturn) {
        visitShowField(parameter.trField(methodReturn), methodReturn.value?.toString(), style)
    }

    private String gridSpanClass(Style style) {
        if (layout != ShowLayout.GRID) return ''
        String cssClass = style?.gridSpanCssClass
        cssClass ? " $cssClass" : ''
    }

    private void writeField(String i18n, String field, Style style, boolean sanitize = true) {
        if (i18n && parameter.target == Parameter.RenderingTarget.MAIL) {
            writeMailField(i18n, field, style)
        } else if (i18n) {
            openFieldList()
            out << """
                <div class="fieldcontain${gridSpanClass(style)}">
                    <dt class="property-label ref-prefix">${i18n}</dt>
                    <dd class="property-value ${style ? style.cssClassesString : ''}" style="${style ? style.cssStyleString : ''}">${field}</dd>
                </div>
        """
        } else {
            closeFieldList()
            out << """<div class="${style ? style.cssClassesString : ''}" style="${style ? style.cssStyleString : ''}">${sanitize ? TaackUiEnablerService.sanitizeString(field) : field}</div>  """
        }
    }

    // Render Show block in mails as list instead of a description list
    private void writeMailField(String i18n, String field, Style style) {
        out << """
                <li class="fieldcontain${gridSpanClass(style)}">
                    <span class="property-label ref-prefix">${i18n}</span>
                    <span class="property-value ${style ? style.cssClassesString : ''}" style="${style ? style.cssStyleString : ''}">${field}</span>
                </li>
        """
    }

    @Override
    void visitShowField(final String i18n, final FieldInfo fieldInfo, final Style style) {
        if (fieldInfo?.value != null) {
            String v = TaackUiEnablerService.sanitizeString(RawHtmlTableDump.dataFormat(fieldInfo.value, null, parameter.lcl))
            if (TaackUiService.contextualMenuClosureFromField(fieldInfo)) {
                String ident = fieldInfo.value.toString()
                String className = fieldInfo.fieldConstraint.field.type.simpleName
                if (GormEntity.isAssignableFrom(fieldInfo.value?.class))
                    ident = (fieldInfo.value as GormEntity).ident()
                else if (parameter.params.containsKey('id')) {
                    ident = parameter.params.long('id')
                    className = fieldInfo.fieldConstraint.field.declaringClass.simpleName
                }
                v = """<span taackContextualMenu="${className + ';' + fieldInfo.fieldName + ';' + ident}">${v}</span>"""

            }
            writeField(i18n, v, style, false)
        }
    }

    @Override
    void visitShowField(final String i18n, final String field, final Style style) {
        if (field) {
            writeField(i18n, field, style)
        }
    }

    @Override
    void visitShowField(String html) {
        closeFieldList()
        out << html
    }

    @Override
    void visitShowAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, Object> additionalParams, boolean isAjax) {
        closeFieldList()
        i18n ?= parameter.trField(controller, action, id != null)
        String url = parameter.urlMapped(controller, action, id, additionalParams)
        if (isAjax && parameter.target != Parameter.RenderingTarget.MAIL) {
            out << """
                     <div class='icon'>
                        <a class='ajaxLink taackShowAction' href="${url}" ajaxAction='${url}'>
                            ${actionIcon.getHtml(i18n)}
                        </a>
                     </div>
                """
        } else {
            out << """
                 <div class='icon'>
                    <a class='link' href="${url}">
                        ${i18n}
                    </a>
                 </div>
                """
        }
    }

    @Override
    void visitShowAction(String i18n, String linkText, String controller, String action, Long id, Map additionalParams, boolean isAjax = true) {
        if (linkText) {
            additionalParams ?= [:]
            additionalParams['isAjax'] = isAjax
            String url = parameter.urlMapped(controller, action, id, additionalParams)
            String link = """<a class="taackShowAction" href="${url}" ${isAjax && parameter.target != Parameter.RenderingTarget.MAIL? """ajaxAction="${url}\"""" : ""}>${linkText}</a>"""
            writeField(i18n, link, null, false)
        }
    }

    @Override
    void visitShowInlineHtml(String html, String additionalCSSClass) {
        closeFieldList()
        out << '</div>'
        out << """<div class="$additionalCSSClass">$html</div>"""
        writeShowWrapperStart()
    }

}
