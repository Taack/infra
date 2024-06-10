package taack.ui.base.form

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.render.TaackUiEnablerService
import taack.ui.base.helper.Utils
import taack.ui.dump.html.base.ButtonStyle

@CompileStatic
class FormSectionSpec extends FormAjaxFieldSpec {
    final static TaackUiEnablerService taackUiEnablerService = Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') as TaackUiEnablerService


    FormSectionSpec(IUiFormVisitor formVisitor) {
        super(formVisitor)
    }

    /**
     * Add a section to enclose fields to display. Can be nested.
     *
     * @param sectionName the label of the section
     * @param width its relative width
     * @param closure Description of the content of this section
     */
    void section(String sectionName, FormSpec.Width width = FormSpec.Width.DEFAULT_WIDTH,
                 @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FormAjaxFieldSpec) Closure closure) {
        formVisitor.visitFormSection(sectionName, width)
        closure.delegate = this
        closure.call()
        formVisitor.visitFormSectionEnd()
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
    void innerFormAction(final MethodClosure action, final Long id = null, final Map params = null) {
        if (taackUiEnablerService.hasAccess(action, id, params)) formVisitor.visitInnerFormAction(null, Utils.getControllerName(action), action.method, id, params, ButtonStyle.SECONDARY)
    }

}
