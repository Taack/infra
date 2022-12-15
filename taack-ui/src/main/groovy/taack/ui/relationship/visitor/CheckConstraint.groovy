package taack.ui.relationship.visitor

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo

@CompileStatic
final class CheckConstraint implements taack.ui.relationship.IConstraintsVisitor {
    final Object object
    boolean isValid = true
    String errorMessage

    CheckConstraint(final Object o) {
        object = o
    }

    @Override
    void notNul(String i18nError, FieldInfo fieldInfo) {
        if (isValid) {
            if (object[fieldInfo.fieldName] == null) {
                isValid = false
                errorMessage = i18nError
            }
        }
    }

    @Override
    void eq(String i18nError, FieldInfo fieldInfo, Object value) {
        if (isValid) {
            if (object[fieldInfo.fieldName] != value) {
                isValid = false
                errorMessage = i18nError
            }
        }
    }

    @Override
    void eq(String i18nError, FieldInfo fieldInfo, FieldInfo value) {
    }

    @Override
    void visitConstraints(FieldInfo fieldInfo) {
    }

    @Override
    void visitConstraintsEnd() {
    }
}
