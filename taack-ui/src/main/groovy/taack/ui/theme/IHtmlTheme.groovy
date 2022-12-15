package taack.ui.theme

import grails.util.Pair
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ui.base.common.Style
import taack.ui.dump.Parameter

interface IHtmlTheme {

    String blockHeader(Long blockId)

    String blockFooter()

    String blockTabHeader(int tabOccurrence, int tabOccurrencePrevious)

    String blockTabFooter()

    String innerBlockDiv()

    String getFormWidthSectionPropertyName()

    String getBlockWidthSectionPropertyName()

    String formContainerHeader()

    String formContainerFooter()

    String formContentHeader()

    String formContentFooter()

    String formSectionHeader(String widthCssProperty, String i18n)

    String formSectionFooter()

    String getFilterFormCssTheme(Class clazz)

    String getFormCssTheme()

    String filterFieldHeader(final String i18n, final String qualifiedName, boolean floatingLabel)

    String filterFieldFooter(final String i18n, final String qualifiedName, boolean floatingLabel)

    String getFormFieldAjaxHeader(String qualifiedName, String i18n)

    String getFormFieldAjaxFooter()

    String formFieldHeader(String i18n, String qualifiedName, boolean isBoolean)

    String formFieldFooter()

    String selectHeader()

    String getSelectCssTheme()

    String selectFooter()

    String radioHeader()

    String getRadioDivCssTheme()

    String getRadioCssTheme()

    String radioLabel(final String i18n, final String qualifiedName, final String value)

    String radioFooter()

    String inputHeader()

    String getFilterInputCssTheme()

    String getInputCssTheme()

    String inputFooter()

    String getTextareaCssTheme()

    String filterButtons(Parameter parameter, List < Pair < String, MethodClosure > > filterAdditionalButtons)

    String filterSectionHeader(String i18n)

    String filterSectionFooter()

    String expressionBoolLabel(String qualifiedName, String i18n)

    String expressionBoolHeader()

    String expressionBoolFooter()

    String getCheckboxCssTheme()

    String getCheckboxFormCssTheme()

    String formColHeader()

    String formColFooter()


    /*TABLE*/

    String tableHeader(Class aClass, String blockId)

    String tableFooter()

    String rowThHeader()

    String rowThFooter()


    /*SHOW*/

    String propertyListHeader()

    String propertyListFooter()

    String showField(String i18n, String field, Style style)

    /*MENU*/

    String menuStartHeader()

    String menuStartFooter()

    String menuHeader(String i18n, String controller, String action, Map<String, ?> params)

    String menuFooter()

    String subMenu(String i18n, String controller, String action, Map<String, ?> params)
}