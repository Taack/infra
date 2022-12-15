package taack.ui.theme

import grails.util.Pair
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.base.common.Style
import taack.ui.base.helper.Utils
import taack.ui.dump.Parameter

class PureCssHtmlTheme implements IHtmlTheme {

    @Override
    String blockHeader(Long blockId) {
        "<div id='blockId${blockId}' class='pure-g taackBlock'>"
    }

    @Override
    String blockFooter() {
        "</div>"
    }

    @Override
    String blockTabHeader(int tabOccurrence, int tabOccurrencePrevious) {
        """<div class="tab${++tabOccurrence}${tabOccurrencePrevious != 0?"Inner":""} pure-g">"""
    }

    @Override
    String blockTabFooter() {
        '</div>'
    }

    @Override
    String innerBlockDiv() {
        "<div class='pure-g'>"
    }

    @Override
    String getFormWidthSectionPropertyName() {
        'sectionCssPureCss'
    }

    @Override
    String getBlockWidthSectionPropertyName() {
        'css'
    }

    @Override
    String formContainerHeader() {
        "<div class='grid-left-content grid-content filter pure-form'>"
    }

    @Override
    String formContainerFooter() {
        "</div>"
    }

    @Override
    String formContentHeader() {
        """<div class="pure-g">"""
    }

    @Override
    String formContentFooter() {
        "</div>"
    }

    @Override
    String formSectionHeader(String widthCssProperty, String i18n) {
    """<div class="${widthCssProperty}">
                    <fieldset>
                    <legend>${i18n}</legend>
                    <div class="pure-g">"""
    }

    @Override
    String formSectionFooter() {
        "</div></fieldset></div>"
    }

    @Override
    String getFilterFormCssTheme(Class clazz) {
        "pure-form pure-form-aligned ${clazz.simpleName.uncapitalize()}-filters filter taackTableFilter"
    }

    @Override
    String getFormCssTheme() {
        "pure-form pure-form-stacked forms taackForm"
    }

    @Override
    String filterFieldHeader(String i18n, String qualifiedName, boolean floatingLabel) {
        """
        <div class="pure-u-1 field-filter ${qualifiedName}">
            <div class="pure-u-1 center-flex"> 
            <label for="${qualifiedName}">
                ${i18n}
            </label>
            </div>
        """
    }

    @Override
    String filterFieldFooter(String i18n, String qualifiedName, boolean floatingLabel) {
        "</div>"
    }

    @Override
    String getFormFieldAjaxHeader(String qualifiedName, String i18n) {
        """
        <div class="pure-u-1">
                <div class="pure-u-1 taackFieldError" taackFieldError="${qualifiedName}" style="display: none;"></div>
                <label for="${qualifiedName}">
                    ${i18n}
                </label>
        """
    }

    @Override
    String getFormFieldAjaxFooter() {
        '</div>'
    }

    @Override
    String formFieldHeader(String i18n, String qualifiedName, boolean isBoolean) {
        """
        <div class="pure-u-1">
                <div class="pure-u-1 taackFieldError" taackFieldError="${qualifiedName}" style="display: none;"></div>
                <div class="pure-u-1 ${qualifiedName}-field-form ${isBoolean ? 'vertical-center ' : ''}">
                    <label for="${qualifiedName}" class="pure-u-22-24">
                        ${i18n}
                    </label>
                </div>
        """
    }

    @Override
    String formFieldFooter() {
        "</div>"
    }

    @Override
    String selectHeader() {
        """
        <div class="center-flex pure-u-1">
        """
    }

    @Override
    String getSelectCssTheme() {
        ""
    }

    @Override
    String selectFooter() {
        "</div>"
    }

    @Override
    String radioHeader() {
        """
        <div class="center-flex pure-u-1">
        """
    }

    @Override
    String getRadioDivCssTheme() {
        "center-flex pure-u-22-24"
    }

    @Override
    String getRadioCssTheme() {
        "many-to-one"
    }

    @Override
    String radioLabel(String i18n, String qualifiedName, String value) {
        """
        <label for="${qualifiedName}Check${value}"> ${i18n} </label>
        """
    }

    @Override
    String radioFooter() {
        "</div>"
    }

    @Override
    String inputHeader() {
        """
        <div class="center-flex pure-u-1">
        """
    }

    @Override
    String getFilterInputCssTheme() {
        ""
    }

    @Override
    String getInputCssTheme() {
        "pure-u-22-24"
    }

    @Override
    String inputFooter() {
        "</div>"
    }

    @Override
    String getTextareaCssTheme() {
        "pure-u-22-24"
    }

    @Override
    String filterButtons(Parameter parameter, List < Pair < String, MethodClosure > > additionalButtons) {
        """
        <div class="filter buttons">
                    <input type="submit" onclick="document.querySelector('[name=offset]').remove();" class="apply taackFilterAction" name="filter" id="filter" value="Filter">
                    <input type="reset" value="Reset" class="reset" >
                </div>
            </form>
    """
    }

    @Override
    String filterSectionHeader(String i18n) {
        """
            <fieldset>
            <div class="pure-u-1 filter-section">
            <legend>${i18n}</legend>
            </div>
            """
    }

    @Override
    String filterSectionFooter() {
        "</fieldset>"
    }

    @Override
    String expressionBoolLabel(String qualifiedName, String i18n) {
        """
<div class="pure-u-1 field-filter ${filterExpression.qualifiedName}">
                    <div class="pure-u-1 center-flex"> 
                    <label for="${filterExpression.qualifiedName}">
                        ${i18n}
                    </label>
                    </div>
"""
    }

    @Override
    String expressionBoolHeader() {
        """<div class="center-flex pure-u-1">"""
    }

    @Override
    String expressionBoolFooter() {
        "</div>"
    }

    @Override
    String getCheckboxFormCssTheme() {
        "pure-22-24"
    }

    @Override
    String getCheckboxCssTheme() {
        """many-to-one pure-u-22-24"""
    }

    @Override
    String formColHeader() {
        "<div class='pure-u-1 pure-u-md-1-2'>"
    }

    @Override
    String formColFooter() {
        "</div>"
    }

    @Override
    String tableHeader(Class aClass, String blockId) {
        """<table class='pure-table ${aClass.simpleName}-table taackTable' taackTableId='${blockId}'>"""
    }

    @Override
    String tableFooter() {
        "</table>"
    }

    @Override
    String rowThHeader() {
        "\n<tr>"
    }

    @Override
    String rowThFooter() {
        "</tr>\n"
    }

    @Override
    String propertyListHeader() {
        "<ol class='property-list taackShow pure-u-1'>"
    }

    @Override
    String propertyListFooter() {
        "</ol>"
    }

    @Override
    String showField(String i18n, String field, Style style) {
        """
                <li class="fieldcontain">
                    <span class="property-label ref-prefix">${i18n}</span>
                    <span class="property-value ${style ? style.cssClassesString : ''}">${field}</span>
                </li> 
        """
    }

    @Override
    String menuStartHeader() {
        """
            <div class="pure-menu pure-menu-horizontal" style="white-space: normal;">
                <ul class="pure-menu-list" style="width: 100%">
        """
    }

    @Override
    String menuStartFooter() {
        "</ul></div>"
    }

    @Override
    String menuHeader(String i18n, String controller = null, String action = null, Map<String, ? extends Object> params = null) {
        if (controller && action && params) {
            return """
            <li class="pure-menu-item pure-menu-has-children pure-menu-allow-hover">
                <a class="pure-menu-link" href="/$controller/$action?${Utils.paramsString(params)}">${i18n}</a>
                <ul class="pure-menu-children">
            """
        } else {
            """
             <li class="pure-menu-item pure-menu-has-children pure-menu-allow-hover">
                <a class="pure-menu-link">${i18n}</a>
                <ul class="pure-menu-children">
            """
        }
    }

    @Override
    String menuFooter() {
        "</ul></li>"
    }

    @Override
    String subMenu(String i18n, String controller, String action, Map<String, ? extends Object> params) {
        """
            <li class="pure-menu-item">
                <a class="pure-menu-link" href="/$controller/$action?${Utils.paramsString(params)}">${i18n}</a>
            </li>
        """
    }


}