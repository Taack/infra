package taack.ui.base.form

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.render.TaackUiEnablerService
import taack.ui.IEnumOption
import taack.ui.IEnumOptions
import taack.ui.base.helper.Utils
import taack.ui.dump.html.base.ButtonStyle

import java.text.NumberFormat

@CompileStatic
class FormAjaxFieldSpec extends FormVisitable {
    final static TaackUiEnablerService taackUiEnablerService = Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') as TaackUiEnablerService

    FormAjaxFieldSpec(final IUiFormVisitor formVisitor) {
        super(formVisitor)
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

    void hiddenField(final FieldInfo field) {
        formVisitor.visitFormHiddenField(field)
    }

    void field(final String i18n = null, final FieldInfo field) {
        formVisitor.visitFormField(i18n, field)
    }

    void field(final String i18n = null, final FieldInfo field, final NumberFormat numberFormat) {
        formVisitor.visitFormField(i18n, field, null, numberFormat)
    }

    void ajaxField(final String i18n = null, final FieldInfo field, final MethodClosure action, final Map<String, ?> params, final FieldInfo<?>... fieldInfos = null) {
        formVisitor.visitFormAjaxField(i18n, Utils.getControllerName(action), action.method, field, (Long)null, params, fieldInfos)
    }

    void ajaxField(final String i18n = null, final FieldInfo field, final MethodClosure action, final Long id = null, final Map<String, ?> params = null) {
        formVisitor.visitFormAjaxField(i18n, Utils.getControllerName(action), action.method, field, id, params)
    }

    void ajaxField(final String i18n = null, final FieldInfo field, final MethodClosure action, final Long id, final FieldInfo<?>... fieldInfos) {
        formVisitor.visitFormAjaxField(i18n, Utils.getControllerName(action), action.method, field, id, null, fieldInfos)
    }

    void ajaxField(final String i18n = null, final FieldInfo field, final MethodClosure action, final FieldInfo<?>... fieldInfos) {
        formVisitor.visitFormAjaxField(i18n, Utils.getControllerName(action), action.method, field, null, null, fieldInfos)
    }

    void ajaxField(final String i18n = null, final FieldInfo field, IEnumOptions enumOptions, final MethodClosure action, final FieldInfo<?>... fieldInfos) {
        formVisitor.visitFormAjaxField(i18n, Utils.getControllerName(action), action.method, field, enumOptions, fieldInfos)
    }

    void field(final String i18n = null, final FieldInfo field, IEnumOptions enumOptions) {
        formVisitor.visitFormField(i18n, field, enumOptions, null)
    }

    void fieldFromMap(final String i18n = null, final FieldInfo field, String mapEntry) {
        formVisitor.visitFormFieldFromMap(i18n, field, mapEntry)
    }

}
