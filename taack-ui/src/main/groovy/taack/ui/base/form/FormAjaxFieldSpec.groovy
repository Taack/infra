package taack.ui.base.form

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ui.EnumOption
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

    void hiddenField(final String name, final String value) {
        formVisitor.visitFormHiddenField(name, value)
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

    void ajaxField(final String i18n = null, final FieldInfo field, EnumOption[] enumOptions, final MethodClosure action, final FieldInfo<?>... fieldInfos) {
        formVisitor.visitFormAjaxField(i18n, Utils.getControllerName(action), action.method, field, enumOptions, fieldInfos)
    }

    void field(final String i18n = null, final FieldInfo field, EnumOption[] enumOptions) {
        formVisitor.visitFormField(i18n, field, enumOptions, null)
    }

    void field(final String i18n, final FieldInfo field, final FieldInfo constrainedField, final Collection<? extends Object> fromCollection) {
        // TODO: for Attachments
    }

    void reverseField(final String i18n, final Class<? extends Object> targetClass, final FieldInfo constrainedField, final Collection<? extends Object> fromCollection, final List<FieldInfo<?>> displayField = null) {
        formVisitor.reverseField(i18n, targetClass, constrainedField, fromCollection, displayField)
    }

    void comment(final String i18n, final String comment) {
        formVisitor.comment(i18n, comment)
    }

    void fieldFromMap(final String i18n = null, final FieldInfo field, String mapEntry) {
        formVisitor.visitFormFieldFromMap(i18n, field, mapEntry)
    }

    void fieldFromColl(final String i18n = null, final FieldInfo field, Object collEntry) {
        formVisitor.visitFormFieldFromColl(i18n, field, collEntry)
    }
}
