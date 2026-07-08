/**
 * Provide classes adding information
 * to the underscore ('_') added symbols.
 * <p>
 * This is made mandatory, because we need the original
 * name of objects passed to method, to compute filters,
 * saves and translations.
 * The Jvm does not give access to parameter caller
 * object names using reflection.
 * <p>
 * This infrastructure also avoid reflection at runtime,
 * adding info at compile time.
 */
package taack.ast.type;

/**
 * Container class holding the meta-information needed by the
 * framework.
 * @param <T> Type of the field
 */
public final class FieldInfo<T> {
    public FieldInfo(final FieldConstraint fieldConstraint, final String fieldName, final T value) {
        this.fieldConstraint = fieldConstraint;
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public String toString() {
        return "FieldInfo{fieldConstraint=" + String.valueOf(getFieldConstraint()) + ", fieldName=" + getFieldName() + " value=" + String.valueOf(getValue()) + "}";
    }

    public final FieldConstraint getFieldConstraint() {
        return fieldConstraint;
    }

    public final String getFieldName() {
        return fieldName;
    }

    public final T getValue() {
        return value;
    }

    /**
     * Data class containing constraints Grails domain constraint
     * closure data.
     */
    private final FieldConstraint fieldConstraint;
    /**
     * Name of the field or name of the field + key if the field is a map.
     */
    private final String fieldName;
    /**
     * Reference to the value of the object
     */
    private final T value;
}
