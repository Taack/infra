package taack.ui.dump.pdf

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.show.IUiShowVisitor
import taack.ui.dump.Parameter
import taack.ui.dump.RawHtmlTableDump

@CompileStatic
final class RawHtmlShowDump implements IUiShowVisitor {
    final private ByteArrayOutputStream out
    final private Parameter parameter

    RawHtmlShowDump(ByteArrayOutputStream out, Parameter parameter) {
        this.out = out
        this.parameter = parameter
    }

    @Override
    void visitShow() {
        out << "<ul class='property-list taackShow pure-u-1'>"
    }

    @Override
    void visitShowEnd() {
        out << '</ul>'
    }

    @Override
    void visitSection(String i18n) {
        out << "<ul class='taackSection'>${i18n?:''}"
    }

    @Override
    void visitSectionEnd() {
        out << '</ul>'
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

    @Override
    void visitShowField(final String i18n, final FieldInfo field, final Style style) {
        if (field?.value) {
            boolean isDiv = style?.isDiv
            final String htmlElement = "${isDiv?'div':'span'}"
            String label = i18n && !i18n.trim().empty ? """<span class="property-label ref-prefix" style="${style?.labelCssStyleString ?:''}">${i18n}</span>""" : ''
            String value = !field.value.toString().trim().empty ? """<${htmlElement} class="property-value ${style?.cssClassesString ?: ''}" style="${style?.cssStyleString ?: ''}">${RawHtmlTableDump.dataFormat(field.value, null, parameter.lcl)}</${htmlElement}>""" : ''
            out << """
                <li class='fieldcontain'>
                    $label
                    $value
                </li>
            """
        }
    }

    @Override
    void visitShowField(final String i18n, final String field, final Style style) {
        if (field) {
            boolean isDiv = style?.isDiv
            final String htmlElement = "${isDiv?'div':'span'}"
            String label = i18n && !i18n.trim().empty ? '''<span class='property-label ref-prefix' style='${style?.labelCssStyleString ?:''}">${i18n}</span>''' : ''
            out << """
                <li class='fieldcontain'>
                    $label
                    <${htmlElement} class="property-value ${style?.cssClassesString ?: ''}' style='${style?.cssStyleString ?: ''}">${field}</${htmlElement}>
                </li>
            """
        }
    }

    @Override
    void visitShowField(String html) {
        out << html
    }

    @Override
    void visitShowInlineHtml(String html, String additionalCSSClass) {
        out << '''<div class='$additionalCSSClass'>'''
        out << html
        out << '</div>'
    }

    @Override
    void visitShowAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, Object> additionalParams, boolean isAjax) {

    }

    @Override
    void visitShowAction(String i18n, String linkText, String controller, String action, Long id, Map additionalParams, boolean isAjax) {

    }
}

