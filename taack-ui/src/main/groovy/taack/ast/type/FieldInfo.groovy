/**
 * Provide classes adding information
 * to the underscore ('_') added symbols.
 *
 * This is made mandatory, because we need the original
 * name of objects passed to method, to compute filters,
 * saves and translations.
 * The Jvm does not give access to parameter caller
 * object names using reflection.
 *
 * This infrastructure also avoid reflection at runtime,
 * adding info at compile time.
 */
package taack.ast.type

import groovy.transform.CompileStatic


/**
 * Container class holding the meta-information needed by the
 * framework.
 * @param <T> Type of the field
 */
@CompileStatic
final class FieldInfo<T> {
    /**
     * Data class containing constraints Grails domain constraint
     * closure data.
     */
    final FieldConstraint fieldConstraint
    /**
     * Name of the field or name of the field + key if the field is a map.
     */
    final String fieldName
    /**
     * Reference to the value of the object
     */
    final T value

    FieldInfo(final FieldConstraint fieldConstraint, final String fieldName, final T value) {
        this.fieldConstraint = fieldConstraint
        this.fieldName = fieldName
        this.value = value
    }

    @Override
    String toString() {
        "FieldInfo{fieldConstraint=$fieldConstraint, fieldName=$fieldName value=$value}"
    }

//    /**
//     * Create a FieldInfo from another FieldInfo that point to a map, using its key.
//     *
//     * @param fieldInfo
//     * @param key
//     * @return FieldInfo for the key
//     */
//    final static FieldInfo mapKey(final FieldInfo fieldInfo, final String key) {
//        new FieldInfo(fieldInfo.fieldConstraint, fieldInfo.fieldName + "_setKey_$key", fieldInfo.value)
//    }
//
//    /**
//     * Create a FieldInfo from another FieldInfo that point to a map, using its key.
//     *
//     * @param fieldInfo
//     * @param key
//     * @return FieldInfo for the value pointed by the map entry
//     */
//    final static FieldInfo mapValue(final FieldInfo fieldInfo, final String key) {
//        // TODO: Should be new FieldInfo(fieldInfo.fieldConstraint, fieldInfo.fieldName + "_setValue_$key", fieldInfo.value[key])
//        new FieldInfo(fieldInfo.fieldConstraint, fieldInfo.fieldName + "_setValue_$key", fieldInfo.value)
//    }

//    final boolean isMapKey() {
//        fieldName.contains('_setKey_')
//    }

//    final boolean isMapValue() {
//        fieldName.contains('_setValue_')
//    }

//    /**
//     * @return Name of the field that contains the map.
//     */
//    final String getMapFieldName() {
//        if (isMapKey()) {
//            int iPoint = fieldName.indexOf('_setKey_')
//            fieldName.substring(0, iPoint)
//        } else if (isMapValue()) {
//            int iPoint = fieldName.indexOf('_setValue_')
//            fieldName.substring(0, iPoint)
//        } else fieldName
//    }
//
//    /**
//     * @return The key value.
//     */
//    final String getMapFieldKey() {
//        if (isMapKey()) {
//            int iPoint = fieldName.indexOf('_setKey_')
//            fieldName.substring(iPoint + 8)
//
//        } else if (isMapValue()) {
//            int iPoint = fieldName.indexOf('_setValue_')
//            fieldName.substring(iPoint + 10)
//        } else null
//    }

//    /**
//     * @return The object value.
//     */
//    final Object getMapValue() {
//        if (isMapValue()) {
//            int iPoint = fieldName.indexOf('_setValue_')
//            return value[fieldName.substring(iPoint + 10)]
//        }
//        null
//    }

//    /**
//     * If value point to an object, or a map, by name
//     * @param name
//     * @return the value of object.name
//     */
//    final String getEmbeddedValue(String name) {
//        String res
//        if (name) {
//            res = value[name]
//        } else if (isMapValue()) {
//            res = getMapValue()
//        } else if (isMapKey()) {
//            res = getMapFieldKey()
//        } else {
//            res = value
//        }
//        res = res ?: ''
//        res
//    }
}
