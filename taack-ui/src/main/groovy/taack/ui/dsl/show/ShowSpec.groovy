package taack.ui.dsl.show

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.dsl.helper.Utils

/**
 * {@link taack.ui.dsl.UiShowSpecifier} delegated class
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
    void section(String sectionName, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = SectionSpec) Closure closure) {
        showVisitor.visitSection(sectionName)
        closure.delegate = this
        closure.call()
        showVisitor.visitSectionEnd()
    }


    /**
     *
     * @param i18n
     * @param action action closure
     * @param id
     * @param params
     * @param isAjax
     */
    void showAction(final MethodClosure action, final Long id = null, final Map params = null) {
        showVisitor.visitShowAction(null, Utils.getControllerName(action), action.method, id, params, true)
    }
}
