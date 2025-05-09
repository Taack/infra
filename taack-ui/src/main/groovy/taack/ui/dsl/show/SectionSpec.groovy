package taack.ui.dsl.show

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.render.TaackUiEnablerService
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.IconStyle
import taack.ui.dsl.common.Style
import taack.ui.dsl.helper.Utils

/**
 * {@link ShowSpec#section(java.lang.String, groovy.lang.Closure)} delegated class
 */
@CompileStatic
class SectionSpec {
    final IUiShowVisitor showVisitor
    TaackUiEnablerService taackUiEnablerService = Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') as TaackUiEnablerService

    SectionSpec(IUiShowVisitor showVisitor) {
        this.showVisitor = showVisitor
    }

    /**
     * Add a field in the show graphical element
     *
     * @param i18n label
     * @param field
     * @param style Portable style
     */
    void field(final Style style = null, final String i18n = null, final FieldInfo field) {
        showVisitor.visitShowField(i18n, field, style)
    }

    /**
     * insert custom HTML in the show element
     *
     * @param html
     * @param style Portable and static element style
     */
    void field(final Style style, final String html) {
        showVisitor.visitShowField(null, html, style)
    }

    /**
     * Add a field in the show graphical element
     *
     * @param i18n label
     * @param field value
     * @param style Portable and static element style
     */
    void field(final Style style = null, final String i18n, final String field) {
        showVisitor.visitShowField(i18n, field, style)
    }

    /**
     * Add a field in the show graphical element, label is automatically deduced.
     *
     * @param style Portable and static element style
     * @param fieldInfos target field
     */
    void fieldLabeled(final Style style = null, FieldInfo... fieldInfos) {
        showVisitor.visitShowFieldLabeled(style, fieldInfos)
    }

    /**
     * Add a field in the show graphical element, label is automatically deduced.
     *
     * @param style Portable and static element style
     * @param methodReturn target a getter field
     */
    void fieldLabeled(final Style style = null, GetMethodReturn methodReturn) {
        showVisitor.visitShowFieldLabeled(style, methodReturn)
    }

    /**
     * field content without labeling
     *
     * @param style
     * @param fieldInfos
     */
    void fieldUnlabeled(final Style style = null, FieldInfo... fieldInfos) {
        showVisitor.visitShowFieldUnLabeled(style, fieldInfos)
    }

    /**
     * field content without labeling
     *
     * @param style
     * @param fieldInfos
     */
    void fieldUnlabeled(final Style style = null, GetMethodReturn methodReturn) {
        showVisitor.visitShowFieldUnLabeled(style, methodReturn)
    }

    /**
     * Action in a form of an icon, that is displayed at the end of the next field value.
     *
     * @param i18n hover text
     * @param icon icon
     * @param action target action if icon is clicked
     * @param id id parameter
     * @param additionalParams target action additional parameters
     */
    void showAction(final String i18n = null, final ActionIcon icon, final MethodClosure action, final Long id, final Map<String, ?> additionalParams) {
        if (taackUiEnablerService.hasAccess(action, id)) showVisitor.visitShowAction(i18n, icon, Utils.getControllerName(action), action.method, id, additionalParams, true)
    }

    void showAction(final String i18n = null, final ActionIcon icon, final MethodClosure action, final Long id) {
        showAction(i18n, icon, action, id, null)
    }

    void showAction(final String i18n = null, final ActionIcon icon, final MethodClosure action, final Map<String, ?> additionalParams) {
        showAction(i18n, icon, action, null, additionalParams)
    }

    void showAction(final MethodClosure action, final Long id, final Map<String, ?> additionalParams = null) {
        showAction(null, ActionIcon.SHOW * IconStyle.SCALE_DOWN, action, id, additionalParams)
    }

    void showAction(final String i18n = null, final String linkText, final MethodClosure action, final Long id, final Map<String, ?> additionalParams) {
        if (taackUiEnablerService.hasAccess(action, id)) {
            showVisitor.visitShowAction(i18n, linkText, Utils.getControllerName(action), action.method, id, additionalParams, true)
        } else {
            showVisitor.visitShowField(i18n, linkText, null)
        }
    }

    void showAction(final String i18n = null, final String linkText, final MethodClosure action, final Long id) {
        showAction(i18n, linkText, action, id, null)
    }

    void showAction(final String i18n = null, final String linkText, final MethodClosure action, final Map<String, ?> additionalParams) {
        showAction(i18n, linkText, action, null, additionalParams)
    }

    /**
     * Inline HTML without style
     *
     * @param html
     */
    void field(final String html) {
        showVisitor.visitShowField(html)
    }

    /**
     * Inline HTML, adding CSS class to the embedding div
     *
     * @param html
     * @param additionalCSSClass
     */
    void inlineHtml(final String html, final String additionalCSSClass = null) {
        showVisitor.visitShowInlineHtml(html, additionalCSSClass)
    }

}
