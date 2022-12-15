package taack.ui.relationship

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo

@CompileStatic
final class ConstraintSpecifier {
    Closure closure
    FieldInfo fieldInfo

    void constraints(final FieldInfo fieldInfo, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = taack.ui.relationship.ConstraintSpec) final Closure closure) {
        this.closure
        this.fieldInfo
    }

    void visitConstraints(final taack.ui.relationship.IConstraintsVisitor constraintsVisitor) {
        if (fieldInfo && closure && constraintsVisitor) {
            constraintsVisitor.visitConstraints(fieldInfo)
            closure.delegate = new taack.ui.relationship.ConstraintSpec(constraintsVisitor)
            closure.call()
            constraintsVisitor.visitConstraintsEnd()
        }
    }
}
