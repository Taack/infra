package taack.ui.base.form

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.EnumOption

import java.text.NumberFormat

@CompileStatic
interface IUiFormVisitor {

    void visitForm(Object aObject, FieldInfo[] lockedFields)

    void visitFormEnd()

    void visitFormSection(final String i18n, FormSpec.Width width)

    void visitFormSectionEnd()

    void visitFormField(final String i18n, final FieldInfo field)

    void visitFormAction(String i18n, String controller, String action, Long id, Map params, boolean isAjax)

    void visitFormFieldFromMap(String i18n, FieldInfo fieldInfo, String mapEntry)

    void visitFormFieldFromColl(String i18n, FieldInfo fieldInfo, Object mapEntry)

    void visitCol()

    void visitColEnd()

    void visitFormHiddenField(FieldInfo fieldInfo)

    void visitFormHiddenField(String name, String value)

    void visitFormField(String i18n, FieldInfo fieldInfo, EnumOption[] enumOptions, NumberFormat numberFormat)

    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo fieldInfo, Long id, Map<String, ?> params, FieldInfo[] fieldInfos)

    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo fieldInfo, EnumOption[] enumOptions, FieldInfo[] fieldInfos)

    void comment(String i18n, String comment)

    void reverseField(String i18n, Class<? extends Object> targetClass, FieldInfo targetField, Collection<? extends Object> constraints, List<FieldInfo> displayField)

    void visitFormSectionTabs(List<String> names, FormSpec.Width width)

    void visitFormSectionTabsEnd()

    void visitFormSectionTab(String name)

    void visitFormSectionTabEnd()
}