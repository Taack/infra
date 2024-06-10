package taack.ui.base.form


import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.base.helper.Utils
import taack.ui.dump.html.base.ButtonStyle
// TODO: Isolate sectionTab
/**
 * {@link taack.ui.base.UiFormSpecifier#ui(java.lang.Object, groovy.lang.Closure)} delegated class.
 *
 * <p>This class allow to draw a form. A form is composed of sections, field and tabs.
 *
 * <p>A form is included into a block calling {@link taack.ui.base.block.BlockSpec#form(java.lang.String, taack.ui.base.UiFormSpecifier)}.
 *
 * <p>Should help with autocompletion and static typing validation of the fields of the form that are displayed in the
 * browser.
 */
@CompileStatic
final class FormSpec extends FormSectionSpec {

    /**
     * Form sections relative Width
     */
    enum Width {
        DEFAULT_WIDTH("col"),
        DOUBLE_WIDTH("col-6"),
        ONE_THIRD("col-4"),
        TWO_THIRD("col-3"),
        FULL_WIDTH("col-12")

        Width(final String sectionCss) {
            this.sectionCss = sectionCss
        }

        final String sectionCss
    }


    FormSpec(IUiFormVisitor formVisitor) {
        super(formVisitor)
    }

    /**
     * {@link #tabs(groovy.lang.Closure)} container.
     *
     * @param width relative total width
     * @param closure list of {@link FormTabSpec#tabLabel(java.lang.String, groovy.lang.Closure)}
     */
    void tabs(Width width = Width.DEFAULT_WIDTH, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FormTabSpec) Closure closure) {
        List<String> tabNames = []

        UiFormVisitorImpl tabNameVisitor = new UiFormVisitorImpl() {
            @Override
            void visitFormTab(String i18n) {
                tabNames << i18n
            }
        }
        closure.delegate = new FormTabSpec(tabNameVisitor)
        closure.call()

        formVisitor.visitFormTabs(tabNames, width)
        closure.delegate = new FormTabSpec(formVisitor)
        closure.call()
        formVisitor.visitFormTabsEnd()
    }

    /**
     * form action. The form is POSTed to the target action.
     *
     * @param i18n label of the button
     * @param action methodClosure pointing to the action
     * @param id id param
     * @param params additional params
     * @param isAjax if true, the action is of ajax kind (either open a modal or updating part of the page, without reloading the page)
     */
    void formAction(final MethodClosure action, final Long id = null, final Map params = null, ButtonStyle style = ButtonStyle.SUCCESS) {
        if (taackUiEnablerService.hasAccess(action, id, params)) formVisitor.visitFormAction(null, Utils.getControllerName(action), action.method, id, params, style)
    }


}
