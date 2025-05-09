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

@CompileStatic
final class RawHtmlShowDump implements IUiShowVisitor {

    final private ByteArrayOutputStream out
    final private Parameter parameter

    final String blockId

    RawHtmlShowDump(final String id, final ByteArrayOutputStream out, final Parameter parameter) {
        this.out = out
        this.parameter = parameter
        this.blockId = id
    }

    @Override
    void visitShow() {

        out << "<div class='property-list taackShow col-12'>"
    }

    @Override
    void visitShowEnd() {
        out << "</div>"
    }

    @Override
    void visitSection(String i18n) {
        out << '<div>'
    }

    @Override
    void visitSectionEnd() {
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

    private static String showField(String i18n, String field, Style style, boolean sanitize = true) {
        if (i18n) {
            """
                <li class="fieldcontain">
                    <span class="property-label ref-prefix">${i18n}</span>
                    <span class="property-value ${style ? style.cssClassesString : ''}" style="${style ? style.cssStyleString : ''}">${field}</span>
                </li> 
        """
        } else {
            """<div class="${style ? style.cssClassesString : ''}" style="${style ? style.cssStyleString : ''}">${sanitize ? TaackUiEnablerService.sanitizeString(field) : field}</div>  """
        }
//        """
//                <li class="fieldcontain">
//                    ${i18n?"""<span class="property-label ref-prefix">${i18n}</span>""":""}
//                    <span class="${i18n?"property-value":""} ${style ? style.cssClassesString : ''}">${field}</span>
//                </li>
//        """
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
            out << showField(i18n, v, style, false)
        }
    }

    @Override
    void visitShowField(final String i18n, final String field, final Style style) {
        if (field) {
            out << showField(i18n, field, style)
        }
    }

    @Override
    void visitShowField(String html) {
        out << html
    }

    @Override
    void visitShowAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, Object> additionalParams, boolean isAjax) {
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
            out << showField(i18n, link, null, false)
        }
    }

    @Override
    void visitShowInlineHtml(String html, String additionalCSSClass) {
        out << "</div>"
        out << """<div class="$additionalCSSClass">$html</div>"""
        out << "<div class='property-list taackShow col-12'>"
    }

}
