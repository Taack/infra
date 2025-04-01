package taack.ui.dsl.form

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.IEnumOptions
import taack.ui.dsl.block.BlockSpec
import taack.ui.dump.html.element.ButtonStyle
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.form.IFormTheme

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
    void visitFormSection(String i18n) {

    }

    @Override
    void visitFormSectionEnd() {

    }

    @Override
    void visitFormField(String i18n, FieldInfo field) {

    }

    @Override
    void visitFormFieldFromMap(String i18n, FieldInfo fieldInfo, String mapEntry, String controller, String action, FieldInfo<?>... fieldInfos) {

    }

    @Override
    void visitColEnd() {

    }

    @Override
    void visitRow() {

    }

    @Override
    void visitRowEnd() {

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
    void visitFormAction(String i18n, String controller, String action, Long id, Map params, ButtonStyle style) {

    }

    @Override
    void visitFormAction(String i18n, String url, ButtonStyle style) {

    }

    @Override
    void visitInnerFormAction(String i18n, String controller, String action, Long id, Map params, ButtonStyle style) {

    }

    @Override
    void visitCol(BlockSpec.Width width) {

    }

    @Override
    void visitFormTabs(List<String> names, BlockSpec.Width width) {

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

    @Override
    void visitTriggerUpdate(FieldInfo<?>... fieldInfos) {

    }
}