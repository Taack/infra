package taack.ast.type;

import java.lang.reflect.Field;

/**
 * Container class with meta-data associated with the object pointer by
 * {@link FieldInfo} class.
 */
public final class FieldConstraint {
    public FieldConstraint(final Constraints constraints, final Field field, final String constraintName) {
        this.constraints = constraints;
        this.field = field;
        this.constraintName = constraintName;
    }

    /**
     * Widget field of the Grails domain constraint for this field
     *
     * @return either null, textarea, ajax, filePath, markdown, passwd...
     * (see {@link WidgetKind})
     */
    public String getWidget() {
        if (constraints == null) return null;
        else return constraints.getWidget();
    }

    /**
     *
     * @return true if the field is nullable, false otherwise
     */
    public boolean getNullable() {
        if (constraints == null) return false;
        else return constraints.getNullable();
    }

    /**
     *
     * @return true if the String contains a mail address
     */
    public boolean getEmail() {
        if (constraints == null) return false;
        else return constraints.getEmail();
    }

    public Integer getMin() {
        if (constraints == null) return null;
        else return constraints.getMin();
    }

    public Integer getMax() {
        if (constraints == null) return null;
        else return constraints.getMax();
    }

    @Override
    public String toString() {
        final Field field = field;
        return "FieldConstraint{constraints=" + String.valueOf(constraints) + ", field=" + (field == null ? null : field.getName()) + ", constraintName='" + constraintName + "'}";
    }

    public final Constraints constraints;
    public final Field field;
    public final String constraintName;

    /**
     * Helper class to manage Grails constraints field.
     */
    public final static class Constraints {
        public Constraints(final String widget, final boolean nullable, final boolean email, final Integer min, final Integer max) {
            this.widget = widget;
            this.nullable = nullable;
            this.email = email;
            this.min = min;
            this.max = max;
        }

        @Override
        public String toString() {
            return "Constraints{widget='" + getWidget() + "', nullable=" + String.valueOf(getNullable()) + ", email=" + String.valueOf(getEmail()) + ", min=" + String.valueOf(getMin()) + ", max=" + String.valueOf(getMax()) + "}";
        }

        public final String getWidget() {
            return widget;
        }

        public final boolean getNullable() {
            return nullable;
        }

        public final boolean isNullable() {
            return nullable;
        }

        public final boolean getEmail() {
            return email;
        }

        public final boolean isEmail() {
            return email;
        }

        public final Integer getMin() {
            return min;
        }

        public final Integer getMax() {
            return max;
        }

        private final String widget;
        private final boolean nullable;
        private final boolean email;
        private final Integer min;
        private final Integer max;
    }
}
