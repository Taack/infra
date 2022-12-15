package taack.ui.relationship

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo

@CompileStatic
final class ConstraintSpec {
    final IConstraintsVisitor constraintsVisitor

    ConstraintSpec(final IConstraintsVisitor constraintsVisitor) {
        this.constraintsVisitor = constraintsVisitor
    }

    void notNull(final String i18nError, final FieldInfo field) {
        constraintsVisitor.notNul(i18nError, field)
    }

    void eq(final String i18nError, final FieldInfo field, Object value) {
        constraintsVisitor.eq(i18nError, field, value)
    }
}
