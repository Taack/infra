package taack.ui.base.filter.expression

import groovy.transform.CompileStatic
import org.apache.http.annotation.Obsolete
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

    final FieldInfo[] operand
    final Operator operator
    final Object value
    final boolean isCollection

    FilterExpression(Long... ids) {
        this.operand = null
        this.isCollection = ids.size() > 1
        this.operator = isCollection ? Operator.IN : Operator.EQ
        this.value = ids
    }

    @Deprecated
    FilterExpression(final FieldInfo operand, final Operator operator, final Object value = null) {
        this.operand = [operand]
        this.isCollection = operand.fieldConstraint.field?.type ? Collection.isAssignableFrom(operand.fieldConstraint.field?.type) : false
        this.operator = operator
        this.value = value
    }

    @Deprecated
    FilterExpression(final FieldInfo[] operand, final Operator operator, final Object value = null) {
        this.operand = operand
        boolean t = false
        for (def o in operand) {
            if (o.fieldConstraint.field?.type && Collection.isAssignableFrom(o.fieldConstraint.field?.type)) t = true
        }
        this.isCollection = t
        this.operator = operator
        this.value = value
    }

    FilterExpression(final Object value = null, final Operator operator, final FieldInfo... operand) {
        this.operand = operand
        boolean t = false
        for (def o in operand) {
            if (o.fieldConstraint.field?.type && Collection.isAssignableFrom(o.fieldConstraint.field?.type)) t = true
        }
        this.isCollection = t
        this.operator = operator
        this.value = value
    }

    String getFieldName() {
        if (!operand) return 'id'
        else {
            operand*.fieldName.join(".")
        }
    }

    String getQualifiedName() {
        "_filterExpression_${fieldName}_${operator}"
    }
}
