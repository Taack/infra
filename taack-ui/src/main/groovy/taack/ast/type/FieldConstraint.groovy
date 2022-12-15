package taack.ast.type

import groovy.transform.CompileStatic

import java.lang.reflect.Field

/**
 * Container class with meta-data associated with the object pointer by
 * {@link FieldInfo} class.
 */
@CompileStatic
final class FieldConstraint {
    FieldConstraint(final Constraints constraints, final Field field, final String constraintName) {
        this.constraints = constraints
        this.field = field
        this.constraintName = constraintName
    }

    /**
     * Helper class to manage Grails constraints field.
     */
    static final class Constraints {
        final String widget
        final boolean nullable
        final boolean email
        final Integer min
        final Integer max

        Constraints(final String widget, final boolean nullable, final boolean email, final Integer min, final Integer max) {
            this.widget = widget
            this.nullable = nullable
            this.email = email
            this.min = min
            this.max = max
        }

        @Override
        String toString() {
            "Constraints{widget='$widget', nullable=$nullable, email=$email, min=$min, max=$max}"
        }
    }
    public final Constraints constraints
    public final Field field
    public final String constraintName

    /**
     * Widget field of the Grails domain constraint for this field
     * @return either null, textarea, ajax, filePath, markdown, passwd...
     * (see {@link WidgetKind})
     */
    String getWidget() {
        if (constraints == null) return null
        else return constraints.widget
    }

    /**
     *
     * @return true if the field is nullable, false otherwise
     */
    boolean getNullable() {
        if (constraints == null) return false
        else return constraints.nullable
    }

    /**
     *
     * @return true if the String contains a mail address
     */
    boolean getEmail() {
        if (constraints == null) return false
        else return constraints.email
    }

    Integer getMin() {
        if (constraints == null) return null
        else return constraints.min
    }

    Integer getMax() {
        if (constraints == null) return null
        else return constraints.max
    }

    @Override
    String toString() {
        "FieldConstraint{constraints=$constraints, field=${field?.name}, constraintName='$constraintName'}"
    }

}
