package taack.ui.dump


import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.IEnumOption
import taack.ui.base.filter.UiFilterVisitorImpl

@CompileStatic
final class RawMapFilterDump extends UiFilterVisitorImpl {

    final Map theResults = [:]
    final Map parameter

    RawMapFilterDump(final Map parameter) {
        this.parameter = parameter
    }

    static String getQualifiedName(final FieldInfo fieldInfo) {
        fieldInfo.fieldName
    }

    static String getQualifiedName(final FieldInfo... fieldInfoList) {
        fieldInfoList*.fieldName.join('.')
    }

    private filterField(final String qualifiedName, final FieldInfo fieldInfo = null, final IEnumOption[] enumOptions = null) {
        final boolean isBoolean = fieldInfo?.fieldConstraint?.field?.type == Boolean
        final boolean isEnum = fieldInfo?.fieldConstraint?.field?.type?.isEnum()
        if (!qualifiedName || fieldInfo?.value?.toString() == null) return
        if (enumOptions) {
            theResults.put(qualifiedName, fieldInfo?.value?.toString())
        } else if (isEnum) {
            theResults.put(qualifiedName, fieldInfo?.value?.toString())
        } else if (isBoolean) {
            Boolean isChecked = parameter[qualifiedName + 'Default'] ?
                    ((parameter[qualifiedName] && parameter[qualifiedName] == '1') ? true : (parameter[qualifiedName] && parameter[qualifiedName] == '0') ? false : null) : fieldInfo.value
            if (isChecked) theResults.put(qualifiedName, isChecked ? '1' : '0')
        } else {
            theResults.put(qualifiedName, fieldInfo?.value?.toString())
        }
    }

    @Override
    void visitFilterField(String i18n, IEnumOption[] enumOptions, FieldInfo[] fields) {
        final String qualifiedName = getQualifiedName(fields)
        filterField(qualifiedName, fields?.last(), enumOptions)
    }

}
