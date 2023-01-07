package taack.ui.base.form

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.base.TaackUiEnablerService
import taack.ui.base.helper.Utils

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
    TaackUiEnablerService taackUiEnablerService = Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') as TaackUiEnablerService

    /**
     * Form sections relative Width
     */
    enum Width {
        DEFAULT_WIDTH("pure-u-1 pure-u-sm-1-2 pure-u-md-1-4"),
        DOUBLE_WIDTH("pure-u-1 pure-u-md-1-2"),
        ONE_THIRD("pure-u-1 pure-u-md-1-3"),
        TWO_THIRD("pure-u-1 pure-u-md-2-3"),
        FULL_WIDTH("pure-u-1")

        Width(final String sectionCss) {
            this.sectionCss = sectionCss
        }

        final String sectionCss
    }


    FormSpec(IUiFormVisitor formVisitor) {
        super(formVisitor)
    }

    /**
     * Add a tabulation to a section tabs. Can only be children of {@link #sectionTabs(groovy.lang.Closure)}.
     *
     * @param sectionName the label in the tab
     * @param closure describe the content of the tabulation
     */
    void sectionTab(String sectionName,
                    @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FormSpec) Closure closure) {
        formVisitor.visitFormSectionTab(sectionName)
        closure.delegate = this
        closure.call()
        formVisitor.visitFormSectionTabEnd()
    }

    /**
     * Add a section to enclose fields to display. Can be nested.
     *
     * @param sectionName the label of the section
     * @param width its relative width
     * @param closure Description of the content of this section
     */
    void section(String sectionName, Width width = Width.DEFAULT_WIDTH,
                 @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FormSectionSpec) Closure closure) {
        formVisitor.visitFormSection(sectionName, width)
        closure.delegate = this
        closure.call()
        formVisitor.visitFormSectionEnd()
    }

    /**
     * {@link #sectionTabs(groovy.lang.Closure)} container.
     *
     * @param width relative total width
     * @param closure list of {@link #sectionTab(java.lang.String, groovy.lang.Closure)}
     */
    void sectionTabs(Width width = Width.DEFAULT_WIDTH, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FormSpec) Closure closure) {
        List<String> tabNames = []
        UiFormVisitorImpl tabNameVisitor = new UiFormVisitorImpl() {
            @Override
            void visitFormSectionTab(String i18n) {
                tabNames << i18n
            }
        }
        closure.delegate = new FormSpec(tabNameVisitor)
        closure.call()

        formVisitor.visitFormSectionTabs(tabNames, width)
        closure.delegate = this
        closure.call()
        formVisitor.visitFormSectionTabsEnd()
    }

    /**
     * form action. The form is POSTed to the target action.
     *
     * @param i18n label of the button
     * @param controller controller holding the action
     * @param action name of the action in the controller
     * @param id id param
     * @param params additional params
     * @param isAjax if true, the action is of ajax kind (either open a modal or updating part of the page, without reloading the page)
     */
    void formAction(final String i18n, final String controller, final String action, final Long id = null, final Map params = null, final boolean isAjax = false) {
        if (taackUiEnablerService.hasAccess(controller, action, id, params)) formVisitor.visitFormAction(i18n, controller, action, id, params, isAjax)
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
    void formAction(final String i18n, final MethodClosure action, final Long id = null, final Map params = null, final boolean isAjax = false) {
        if (taackUiEnablerService.hasAccess(action, id, params)) formVisitor.visitFormAction(i18n, Utils.getControllerName(action), action.method, id, params, isAjax)
    }

    /**
     * form action. The form is POSTed to the target action.
     *
     * @param i18n label of the button
     * @param action methodClosure pointing to the action
     * @param id id param
     * @param isAjax if true, the action is of ajax kind (either open a modal or updating part of the page, without reloading the page)
     */
    void formAction(final String i18n, final MethodClosure action, final Long id, final boolean isAjax) {
        if (taackUiEnablerService.hasAccess(action, id)) formVisitor.visitFormAction(i18n, Utils.getControllerName(action), action.method, id, null, isAjax)
    }

    /**
     * form action. The form is POSTed to the target action.
     *
     * @param i18n label of the button
     * @param action methodClosure pointing to the action
     * @param params additional params
     * @param isAjax if true, the action is of ajax kind (either open a modal or updating part of the page, without reloading the page)
     */
    void formAction(final String i18n, final MethodClosure action, final Map params, final boolean isAjax = false) {
        if (taackUiEnablerService.hasAccess(action, params)) formVisitor.visitFormAction(i18n, Utils.getControllerName(action), action.method, null as Long, params, isAjax)
    }

    /**
     * form action. The form is POSTed to the target action.
     *
     * @param i18n label of the button
     * @param action methodClosure pointing to the action
     * @param isAjax if true, the action is of ajax kind (either open a modal or updating part of the page, without reloading the page)
     */
    void formAction(final String i18n, final MethodClosure action, final boolean isAjax) {
        if (taackUiEnablerService.hasAccess(action)) formVisitor.visitFormAction(i18n, Utils.getControllerName(action), action.method, null, null, isAjax)
    }
}
