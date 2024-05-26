package taack.ui.base.form

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ui.IEnumOption
import taack.ui.IEnumOptions
import taack.ui.base.helper.Utils

import java.text.NumberFormat

@CompileStatic
class FormAjaxFieldSpec {
    final IUiFormVisitor formVisitor


    FormAjaxFieldSpec(final IUiFormVisitor formVisitor) {
        this.formVisitor = formVisitor
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
