package taack.ui.base.form

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.EnumOption

import java.text.NumberFormat

@CompileStatic
class UiFormVisitorImpl implements IUiFormVisitor {

    @Override
    void visitForm(Object aObject, FieldInfo[] lockedFields = null) {

    }

    @Override
    void visitFormEnd() {

    }

    @Override
    void visitFormSectionEnd() {

    }

    @Override
    void visitFormField(String i18n, FieldInfo field) {

    }

    @Override
    void visitFormAction(String i18n, String controller, String action, Long id, Map params, boolean isAjax = false) {

    }

    @Override
    void visitFormFieldFromMap(String i18n, FieldInfo fieldInfo, String mapEntry) {

    }

    @Override
    void visitFormFieldFromColl(String i18n, FieldInfo fieldInfo, Object collEntry) {

    }

    @Override
    void visitCol() {

    }

    @Override
    void visitColEnd() {

    }

    @Override
    void visitFormHiddenField(FieldInfo fieldInfo) {

    }

    @Override
    void visitFormHiddenField(String name, String value) {

    }

    @Override
    void visitFormField(String i18n, FieldInfo fieldInfo, EnumOption[] enumOptions, NumberFormat numberFormat) {

    }

    @Override
    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo fieldInfo, Long id, Map<String, ?> params, FieldInfo[] fieldInfos) {

    }

    @Override
    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo fieldInfo, EnumOption[] enumOptions, FieldInfo[] fieldInfos) {

    }

    @Override
    void comment(String i18n, String comment) {

    }

    @Override
    void reverseField(String i18n, Class<? extends Object> targetClass, FieldInfo targetField, Collection<? extends Object> constraints, List<FieldInfo> displayField) {

    }

    @Override
    void visitFormSection(String i18n, FormSpec.Width width) {

    }

    @Override
    void visitFormSectionTabs(List<String> names, FormSpec.Width width) {

    }

    @Override
    void visitFormSectionTabsEnd() {

    }

    @Override
    void visitFormSectionTab(String name) {

    }

    @Override
    void visitFormSectionTabEnd() {

    }
}
