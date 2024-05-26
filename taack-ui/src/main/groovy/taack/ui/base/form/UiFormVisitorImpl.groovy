package taack.ui.base.form

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.IEnumOption
import taack.ui.IEnumOptions

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
    void visitCol() {

    }

    @Override
    void visitColEnd() {

    }

    @Override
    void visitFormHiddenField(FieldInfo fieldInfo) {

    }

    @Override
    void visitFormField(String i18n, FieldInfo fieldInfo, IEnumOptions enumOptions, NumberFormat numberFormat) {

    }

    @Override
    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo fieldInfo, Long id, Map<String, ?> params, FieldInfo[] fieldInfos) {

    }

    @Override
    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo fieldInfo, IEnumOptions enumOptions, FieldInfo[] fieldInfos) {

    }

    @Override
    void visitFormSection(String i18n, FormSpec.Width width) {

    }

    @Override
    void visitFormTabs(List<String> names, FormSpec.Width width) {

    }

    @Override
    void visitFormTabsEnd() {

    }

    @Override
    void visitFormTab(String name) {

    }

    @Override
    void visitFormTabEnd() {

    }
}
