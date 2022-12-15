package taack.ui.base.show

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.base.helper.Utils

/**
 * {@link taack.ui.base.UiShowSpecifier} delegated class
 *
 * <p>This class allows to add fields to show, with some style. The show block element is not editable.
 */
@CompileStatic
class ShowSpec extends SectionSpec {

    ShowSpec(IUiShowVisitor showVisitor) {
        super(showVisitor)
    }

    /**
     * Add section to group field together
     *
     * @param sectionName label of the section
     * @param closure list of fields to display
     */
    void section(String sectionName, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = SectionSpec) Closure closure) {
        showVisitor.visitSection(sectionName)
        closure.delegate = this
        closure.call()
        showVisitor.visitSectionEnd()
    }

    /**
     * Link action to insert in the show block
     *
     * @param i18n label of the action
     * @param controller controller targeted by a click on the link
     * @param action action targeted by a click on the link
     * @param id object ID to pass
     * @param params additional params
     * @param isAjax if true, target action is an ajax one
     */
    void showAction(final String i18n, final String controller, final String action, final Long id = null, final Map params = null, boolean isAjax = true) {
        showVisitor.visitShowAction(i18n, controller, action, id, params, isAjax)
    }

    /**
     * see {@link #showAction(java.lang.String, java.lang.String, java.lang.String, java.lang.Long, java.util.Map)}
     *
     * @param i18n
     * @param action action closure
     * @param id
     * @param params
     * @param isAjax
     */
    void showAction(final String i18n, final MethodClosure action, final Long id = null, final Map params = null, boolean isAjax = true) {
        showVisitor.visitShowAction(i18n, Utils.getControllerName(action), action.method, id, params, isAjax)
    }

    /**
     * see {@link #showAction(java.lang.String, java.lang.String, java.lang.String, java.lang.Long, java.util.Map)}
     *
     * @param i18n
     * @param action
     * @param id
     * @param isAjax
     */
    void showAction(final String i18n, final MethodClosure action, final Long id, boolean isAjax) {
        showVisitor.visitShowAction(i18n, Utils.getControllerName(action), action.method, id, null, isAjax)
    }

}
