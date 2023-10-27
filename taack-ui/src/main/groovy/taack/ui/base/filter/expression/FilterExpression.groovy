package taack.ui.base.filter.expression

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo

enum Operator {
    IN,
    NI, // Not in
    EQ,
    NE,
    LT,
    GT,
    IS_EMPTY
}

@CompileStatic
final class FilterExpression {

    final String fieldName
    final Operator operator
    final Object value
    final boolean isCollection

    FilterExpression(Long... ids) {
        this.fieldName = 'id'
        this.isCollection = ids.size() > 1
        this.operator = isCollection ? Operator.IN : Operator.EQ
        this.value = ids
    }

    FilterExpression(final FieldInfo operand, final Operator operator, final Object value = null) {
        this.fieldName = operand.fieldName
        this.isCollection = operand.fieldConstraint.field?.type ? Collection.isAssignableFrom(operand.fieldConstraint.field?.type) : false
        this.operator = operator
        this.value = value
    }

    FilterExpression(final FieldInfo[] operand, final Operator operator, final Object value = null) {
        this.fieldName = operand*.fieldName.join(".")
        boolean t = false
        for (def o in operand) {
            if (o.fieldConstraint.field?.type && Collection.isAssignableFrom(o.fieldConstraint.field?.type)) t = true
        }
        this.isCollection = t
        this.operator = operator
        this.value = value
    }

    String getQualifiedName() {
        "_filterExpression_${fieldName}_${operator}"
    }
}
