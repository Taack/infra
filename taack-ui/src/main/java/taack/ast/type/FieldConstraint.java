package taack.ast.type;

import java.lang.reflect.Field;

/**
 * Container class with metadata associated with the object pointer by
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
     * @return either null, textarea, ajax, filePath, Markdown, passwd...
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

    public Number getMin() {
        if (constraints == null) return null;
        else return constraints.getMin();
    }

    public Number getMax() {
        if (constraints == null) return null;
        else return constraints.getMax();
    }

    @Override
    public String toString() {
        return "FieldConstraint{constraints=" + constraints + ", field=" + (field == null ? null : field.getName()) + ", constraintName='" + constraintName + "'}";
    }

    public final Constraints constraints;
    public final Field field;
    public final String constraintName;

    /**
     * Helper class to manage Grails constraints field.
     */
    public final static class Constraints {
        public Constraints(final String widget, final boolean nullable, final boolean email, final Number min, final Number max) {
            this.widget = widget;
            this.nullable = nullable;
            this.email = email;
            this.min = min;
            this.max = max;
        }

        @Override
        public String toString() {
            return "Constraints{widget='" + getWidget() + "', nullable=" + getNullable() + ", email=" + getEmail() + ", min=" + getMin() + ", max=" + getMax() + "}";
        }

        public String getWidget() {
            return widget;
        }

        public boolean getNullable() {
            return nullable;
        }

        public boolean isNullable() {
            return nullable;
        }

        public boolean getEmail() {
            return email;
        }

        public boolean isEmail() {
            return email;
        }

        public Number getMin() {
            return min;
        }

        public Number getMax() {
            return max;
        }

        private final String widget;
        private final boolean nullable;
        private final boolean email;
        private final Number min;
        private final Number max;
    }
}
