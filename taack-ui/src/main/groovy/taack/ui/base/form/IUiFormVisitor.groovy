package taack.ui.base.form

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.IEnumOptions
import taack.ui.dump.html.base.ButtonStyle
import taack.ui.dump.html.base.IHTMLElement
import taack.ui.dump.html.base.TaackTag
import taack.ui.dump.html.form.IFormTheme

import java.text.NumberFormat

@CompileStatic
interface IUiFormVisitor extends IBaseFormVisitor {

    void visitForm(Object aObject, FieldInfo[] lockedFields)

    void visitFormEnd()

    void visitFormSection(final String i18n, FormSpec.Width width)

    void visitFormSectionEnd()

    void visitFormField(final String i18n, final FieldInfo field)

    void visitFormFieldFromMap(String i18n, FieldInfo fieldInfo, String mapEntry)

    void visitFormHiddenField(FieldInfo fieldInfo)

    void visitFormField(String i18n, FieldInfo fieldInfo, IEnumOptions enumOptions, NumberFormat numberFormat)

    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo fieldInfo, Long id, Map<String, ?> params, FieldInfo[] fieldInfos)

    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo fieldInfo, IEnumOptions enumOptions, FieldInfo[] fieldInfos)

    void visitFormAction(String i18n, String controller, String action, Long id, Map params, ButtonStyle style)

    void visitInnerFormAction(String i18n, String controller, String action, Long id, Map params, ButtonStyle style)

    void visitCol()

    void visitColEnd()

    void visitFormTabs(List<String> names, FormSpec.Width width)

    void visitFormTabsEnd()

    void visitFormTab(String name)

    void visitFormTabEnd()

}