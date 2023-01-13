package taack.ui.base.show

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.base.TaackUiEnablerService
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.base.helper.Utils

// TODO: rename fieldAction to showAction
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
    void field(final String i18n, final FieldInfo field, final Style style = null) {
        showVisitor.visitShowField(i18n, field, style)
    }

    /**
     * insert custom HTML in the show element
     *
     * @param html
     * @param style Portable and static element style
     */
    void field(final String html, final Style style) {
        showVisitor.visitShowField(null, html, style)
    }

    /**
     * Add a field in the show graphical element
     *
     * @param i18n label
     * @param field value
     * @param style Portable and static element style
     */
    void field(final String i18n, final String field, final Style style = null) {
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
     * @param isAjax true if target action is an ajax one
     */
    void fieldAction(final String i18n, final ActionIcon icon, final MethodClosure action, final Long id = null, final boolean isAjax = true) {
        if (taackUiEnablerService.hasAccess(action, id)) showVisitor.visitFieldAction(i18n, icon, Utils.getControllerName(action), action.method, id, null, isAjax)
    }

    /**
     * see {@link #fieldAction(java.lang.String, taack.ui.base.common.ActionIcon, org.codehaus.groovy.runtime.MethodClosure, java.lang.Long, boolean)}
     *
     * @param i18n hover text
     * @param icon
     * @param action target action if icon is clicked
     * @param additionalParams target action additional parameters
     * @param isAjax true if target action is an ajax one
     */
    void fieldAction(final String i18n, final ActionIcon icon, final MethodClosure action, final Map<String, ?> additionalParams, final boolean isAjax = true) {
        if (taackUiEnablerService.hasAccess(action, additionalParams)) showVisitor.visitFieldAction(i18n, icon, Utils.getControllerName(action), action.method, null, additionalParams, isAjax)
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
    void inlineHtml(final String html, final String additionalCSSClass) {
        showVisitor.visitShowInlineHtml(html, additionalCSSClass)
    }

    /**
     * Editable field
     *
     * @param i18n label
     * @param field target field (require action and object to be set in {@link taack.ui.base.UiShowSpecifier#ui(java.lang.Object, org.codehaus.groovy.runtime.MethodClosure, groovy.lang.Closure)}
     * @param isAjax if true, the target action is an ajax one
     */
    void showInputField(final String i18n, final FieldInfo field, final boolean isAjax = true) {
        showVisitor.visitShowInputField(i18n, field, isAjax)
    }
}
