package taack.ui.relationship

import taack.ast.type.FieldInfo

interface IConstraintsVisitor {

    void notNul(String i18nError, FieldInfo fieldInfo)

    void eq(String i18nError, FieldInfo fieldInfo, Object value)

    void eq(String i18nError, FieldInfo fieldInfo, FieldInfo value)

    void visitConstraints(FieldInfo fieldInfo)

    void visitConstraintsEnd()

}