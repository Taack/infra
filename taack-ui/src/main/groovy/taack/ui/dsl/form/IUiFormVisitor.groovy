package taack.ui.dsl.form

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.IEnumOptions
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.block.BlockSpec.Width
import taack.ui.dump.html.element.ButtonStyle

import java.text.NumberFormat

@CompileStatic
interface IUiFormVisitor {

    void visitTriggerUpdate(FieldInfo<?>... fieldInfos)

    void visitForm(Object aObject, FieldInfo[] lockedFields)

    void visitFormEnd()

    void visitFormSection(final String i18n)

    void visitFormSectionEnd()

    void visitFormField(final String i18n, final FieldInfo field)

    void visitFormFieldFromMap(String i18n, FieldInfo fieldInfo, String mapEntry, String controller, String action, FieldInfo<?>... fieldInfos)

    void visitFormHiddenField(FieldInfo fieldInfo)

    void visitFormField(String i18n, FieldInfo fieldInfo, IEnumOptions enumOptions, NumberFormat numberFormat)

    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo fieldInfo, Long id, Map<String, ?> params, FieldInfo[] fieldInfos)

    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo fieldInfo, IEnumOptions enumOptions, FieldInfo[] fieldInfos)

    void visitFormAction(String i18n, String controller, String action, Long id, Map params, ButtonStyle style)

    void visitFormAction(String i18n, String url, ButtonStyle style)

    void visitInnerFormAction(String i18n, String controller, String action, Long id, Map params, ButtonStyle style)

    void visitCol(Width width)

    void visitColEnd()

    void visitRow()

    void visitRowEnd()

    void visitFormTabs(List<String> names, BlockSpec.Width width)

    void visitFormTabsEnd()

    void visitFormTab(String name)

    void visitFormTabEnd()

}