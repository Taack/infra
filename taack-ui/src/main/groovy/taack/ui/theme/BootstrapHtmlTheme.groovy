package taack.ui.theme

import grails.util.Pair
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.base.common.Style
import taack.ui.base.helper.Utils
import taack.ui.dump.Parameter

@CompileStatic
final class BootstrapHtmlTheme implements IHtmlTheme {

    @Override
    String blockHeader(Long blockId) {
        "<div id='blockId${blockId}' class='taackBlock bootstrap-block row'>"
    }

    @Override
    String blockFooter() {
        "</div>"
    }

    @Override
    String blockTabHeader(int tabOccurrence, int tabOccurrencePrevious) {
        """<div class="tab${++tabOccurrence}${tabOccurrencePrevious != 0?"Inner":""} row">"""
    }

    @Override
    String blockTabFooter() {
        '</div>'
    }

    String innerBlockDiv() {
        "<div class='row'>"
    }

    @Override
    String getFormWidthSectionPropertyName() {
        'sectionCssBootstrap'
    }

    @Override
    String getBlockWidthSectionPropertyName() {
        'bootstrapCss'
    }

    @Override
    String formContainerHeader() {
        "<div class='grid-left-content grid-content filter'>"
    }

    @Override
    String formContainerFooter() {
        "</div>"
    }

    @Override
    String formContentHeader() {
        """<div class="row">"""
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
                    <div class="row">"""
    }

    @Override
    String formSectionFooter() {
        "</div></fieldset></div>"
    }

    @Override
    String getFilterFormCssTheme(Class clazz) {
        "${clazz.simpleName.uncapitalize()}-filters filter taackTableFilter"
    }

    @Override
    String getFormCssTheme() {
        "forms taackForm"
    }

    @Override
    String filterFieldHeader(String i18n, String qualifiedName, boolean floatingLabel) {
        if (floatingLabel) {
            """
                <div class="mb-2 field-filter ${qualifiedName} form-label-group">
            """
        } else {
            """
            <div class="mb-1 mt-2 field-filter ${qualifiedName}">
                <div class="center-flex"> 
                <label for="${qualifiedName}" class="label-form">
                    ${i18n}
                </label>
            </div>
            """
        }
    }

    @Override
    String filterFieldFooter(String i18n, String qualifiedName, boolean floatingLabel) {
        if (floatingLabel) {
            """
                <label for="${qualifiedName}" class="label-form">
                    ${i18n}
                </label>
            </div>"""
        } else {
            """</div>"""
        }
    }

    @Override
    String getFormFieldAjaxHeader(String qualifiedName, String i18n) {
        """
        <div class="col-12">
                <div class="col-12 taackFieldError" taackFieldError="${qualifiedName}" style="display: none;"></div>
                <label for="${qualifiedName}" class='label-form'>
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
        <div class="col-12">
                <div class="col-12 taackFieldError" taackFieldError="${qualifiedName}" style="display: none;"></div>
                <div class="col-12 ${qualifiedName}-field-form">
                    <label for="${qualifiedName}" class="col-11 form-label">
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
            <div>
        """
    }

    @Override
    String getSelectCssTheme() {
        "custom-select custom-select-sm"
    }

    @Override
    String selectFooter() {
        "</div>"
    }

    @Override
    String radioHeader() {
        """
        <div class="center-flex">
        """
    }

    @Override
    String getRadioDivCssTheme() {
        "center-flex col-4"
    }

    @Override
    String getRadioCssTheme() {
        "many-to-one"
    }

    @Override
    String radioLabel(String i18n, String qualifiedName, String value) {
        """
        <label for="${qualifiedName}Check${value}" class="form-label"> ${i18n} </label>
        """
    }

    @Override
    String radioFooter() {
        "</div>"
    }

    @Override
    String inputHeader() {
        """
        <div class="center-flex">
        """
    }

    @Override
    String getFilterInputCssTheme() {
        "form-control form-control-sm"
    }

    @Override
    String getInputCssTheme() {
        "col-11 form-control form-control-sm"
    }

    @Override
    String inputFooter() {
        "</div>"
    }

    @Override
    String getTextareaCssTheme() {
        "col-11 form-control form-control-sm"
    }

    @Override
    String filterButtons(Parameter parameter, List < Pair < String, MethodClosure > > filterAdditionalButtons) {
        StringBuffer ab = new StringBuffer()
        if (filterAdditionalButtons && !filterAdditionalButtons.empty) {
            filterAdditionalButtons.eachWithIndex { it, occ ->
                String i18n = it.aValue
                String controller = Utils.getControllerName(it.bValue)
                String action = it.bValue.method
                ab.append("""
                    <button type="submit" formaction="/$controller/$action" onclick="document.querySelector('[name=offset]').remove();" class="taackFilterAction w-75 btn btn-sm btn-secondary mb-2" name="additionAction-$occ" id="additionAction-$occ" value="Filter">${i18n}</button>
                """)
            }
        }
        """
        <div class="filter buttons text-center">
        <button type="submit" formaction="/${parameter.applicationTagLib.controllerName}/${parameter.applicationTagLib.actionName}" onclick="document.querySelector('[name=offset]').remove();" class="taackFilterAction w-75 btn btn-sm btn-success mb-2" name="filter" id="filter" value="Filter">Filter</button>
        ${ab}
        <button type="reset" value="Reset" class="w-75 btn btn-sm btn-warning">Reset</button>
        </div>
        </form>
    """
    }

    @Override
    String filterSectionHeader(String i18n) {
        """
            <fieldset>
            <div class="bootstrap filter-section mb-2">
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
<div class="field-filter ${qualifiedName}">
                    <div class=" center-flex"> 
                    <label for="${qualifiedName}">
                        ${i18n}
                    </label>
                    </div>
"""
    }

    @Override
    String expressionBoolHeader() {
        """<div class="center-flex col-12">"""
    }

    @Override
    String expressionBoolFooter() {
        "</div>"
    }

    @Override
    String getCheckboxFormCssTheme() {
        "form-check-input col-11"
    }

    @Override
    String getCheckboxCssTheme() {
        """many-to-one col-11"""
    }

    @Override
    String formColHeader() {
        "<div class='col-12 col-md-6'>"
    }

    @Override
    String formColFooter() {
        "</div>"
    }

    @Override
    String tableHeader(Class aClass, String blockId) {
        """<table class='bootstrap-table table table-bordered table-striped border ${aClass.simpleName}-table taackTable' taackTableId='${blockId}'>"""
    }

    @Override
    String tableFooter() {
        "</table>"
    }


    @Override
    String rowThHeader() {
        "\n<thead><tr>"
    }

    @Override
    String rowThFooter() {
        "</tr></thead>\n"
    }

    @Override
    String propertyListHeader() {
        "<div class='property-list taackShow col-12'>"
    }

    @Override
    String propertyListFooter() {
        "</div>"
    }

    @Override
    String showField(String i18n, String field, Style style) {
        if (i18n) {
            """
                <li class="fieldcontain">
                    <span class="property-label ref-prefix">${i18n}</span>
                    <span class="property-value ${style ? style.cssClassesString : ''}">${field}</span>
                </li> 
        """
        } else {
            """<div class="${style ? style.cssClassesString : ''}">${field}</div>  """
        }
        """
                <li class="fieldcontain">
                    ${i18n?"""<span class="property-label ref-prefix">${i18n}</span>""":""}
                    <span class="${i18n?"property-value":""} ${style ? style.cssClassesString : ''}">${field}</span>
                </li> 
        """
    }

    @Override
    String menuStartHeader() {
        """
            <nav class="navbar navbar-expand">
            <div class="navbar" id="navbarSupportedContent">
                <ul class="navbar-nav mr-auto" style="width: 100%" aria-labelledby="dLabel">
        """
    }

    @Override
    String menuStartFooter() {
        "</ul></div></nav>"
    }

    @Override
    String menuHeader(String i18n, String controller = null, String action = null, Map<String, ? extends Object> params = null) {
        if (controller && action && params) {
            return """
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false" href="/$controller/$action?${Utils.paramsString(params)}">${i18n}</a>
                <ul class="dropdown-menu">
            """
        } else {
            """
             <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">${i18n}</a>
                <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
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
            <li class="nav-item dropdown">
                <a class="nav-link" href="/$controller/$action?${Utils.paramsString(params)}">${i18n}</a>
            </li>
        """
    }
}
