package taack.ui.dump.mail

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.base.show.IUiShowVisitor
import taack.ui.dump.Parameter

@CompileStatic
final class RawHtmlShowDump implements IUiShowVisitor {
    final private ByteArrayOutputStream out
    final private Parameter parameter
    private Object aObject

    RawHtmlShowDump(ByteArrayOutputStream out, Parameter parameter) {
        this.out = out
        this.parameter = parameter
    }

    @Override
    void visitShow(Object currentObject, String controller = null, String action = null) {
        this.aObject = currentObject
        parameter.aClassSimpleName = currentObject?.class?.simpleName
        out << "<div class='property-list taackShow pure-u-1'>"
    }

    @Override
    void visitShowEnd() {
        out << "</div>"
    }

    @Override
    void visitSection(String i18n) {
        out << "<span class='taackSection'>${i18n?:''}"
    }

    @Override
    void visitSectionEnd() {
        out << "</span>"
    }

    @Override
    void visitShowFieldUnLabeled(Style style, FieldInfo... fields) {
        visitShowField(null, fields.last().value?.toString(), style)
    }

    @Override
    void visitShowFieldLabeled(Style style, FieldInfo... fields) {
        visitShowField(parameter.trField(fields), fields.last().value?.toString(), style)
    }

    @Override
    void visitShowFieldUnLabeled(Style style, GetMethodReturn methodReturn) {
        visitShowField(null, methodReturn.value?.toString(), style)
    }

    @Override
    void visitShowFieldLabeled(Style style, GetMethodReturn methodReturn) {
        visitShowField(parameter.trField(methodReturn), methodReturn.value?.toString(), style)
    }

    @Override
    void visitShowField(final String i18n, final FieldInfo field, final Style style) {
        if (field.value) {
            boolean isDiv = style?.isDiv
            final String htmlElement = "${isDiv?"div":"span"}"
            out << """
                <div class="fieldcontain">
                    <span style="${style?.labelCssStyleString ?:""};color: blue;">${i18n ?: ''}</span>
                    <${htmlElement} class="${style?.cssClassesString ?: ''}" style="${style?.cssStyleString ?: ''}">${field.value.toString()}</${htmlElement}>
                </div>
            """
        }
    }

    @Override
    void visitShowField(final String i18n, final String field, final Style style) {
        if (field) {
            boolean isDiv = style?.isDiv
            final String htmlElement = "${isDiv?"div":"span"}"
            out << """
                <div class="fieldcontain">
                    <span class="property-label ref-prefix" style="${style?.labelCssStyleString ?:""}">${i18n ?: ''}</span>
                    <${htmlElement} class="property-value ${style?.cssClassesString ?: ''}" style="${style?.cssStyleString ?: ''}">${field}</${htmlElement}>
                </div>
            """
        }
    }

    @Override
    void visitShowField(String html) {
        out << html
    }

    @Override
    void visitShowAction(String i18n, String controller, String action, Long id, Map additionalParams, boolean isAjax = true) {
    }

    @Override
    void visitShowInputField(String i18n, FieldInfo field, boolean isAjax = false) {
        visitShowField(i18n, field, null)
    }

    @Override
    void visitFieldAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, Object> additionalParams, boolean isAjax) {
    }

    @Override
    void visitShowInlineHtml(String html, String additionalCSSClass) {

    }
}

