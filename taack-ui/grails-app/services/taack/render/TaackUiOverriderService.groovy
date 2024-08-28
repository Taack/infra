package taack.render

import grails.compiler.GrailsCompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo

import java.lang.reflect.Field

interface IShowOverrider<T> {
    String getImagePreview(T t, FieldInfo fieldInfo)

    String getTextSnippet(T t, FieldInfo fieldInfo)
}

interface IFormInputOverrider<U> extends IShowOverrider<U> {
    String getValue(U t, FieldInfo fieldInfo)
}

@GrailsCompileStatic
final class TaackUiOverriderService {

    private final static Map<Field, IFormInputOverrider> inputToOverrides = [:]
    private final static Map<Class, IFormInputOverrider> inputToOverridesClasses = [:]
    private final static Map<Class, IShowOverrider> showOverridesClasses = [:]

    /**
     * Execute the closure if the actions are a target of a link. If the closure returns true,
     * the action is allowed, if false, it cannot be reached.
     *
     * @param closure must return true or false
     * @param actions list of actions that are secured by the closure
     */
    static void addInputToOverride(IFormInputOverrider inputOverrider, FieldInfo fieldInfo) {
        inputToOverrides.put(fieldInfo.fieldConstraint.field, inputOverrider)
    }

    static <Z extends GormEntity<Z>> void addInputToOverride(IFormInputOverrider inputOverrider, Class<Z> aClass) {
        inputToOverridesClasses.put(aClass, inputOverrider)
    }

    static <Z extends GormEntity<Z>> void addShowToOverride(IFormInputOverrider inputOverrider, Class<Z> aClass) {
        showOverridesClasses.put(aClass, inputOverrider)
    }

    static boolean hasInputOverride(FieldInfo fieldInfo) {
        inputToOverrides.containsKey(fieldInfo.fieldConstraint.field) || inputToOverridesClasses.containsKey(fieldInfo.fieldConstraint.field.type)
    }

    static <Z extends GormEntity<Z>> String formInputPreview(Z t, FieldInfo fieldInfo) {
        if (!t && fieldInfo) inputToOverridesClasses.get(fieldInfo.fieldConstraint.field.type)?.getImagePreview(fieldInfo.value, fieldInfo)
        else if (t && fieldInfo) inputToOverrides.get(fieldInfo.fieldConstraint.field)?.getImagePreview(t, fieldInfo) ?: fieldInfo.value ? inputToOverridesClasses.get(fieldInfo.fieldConstraint.field.type)?.getImagePreview(fieldInfo.value, fieldInfo) : null
        else null
    }

    static <Z extends GormEntity<Z>> String formInputSnippet(Z t, FieldInfo fieldInfo) {
        if (!t && fieldInfo) inputToOverridesClasses.get(fieldInfo.fieldConstraint.field.type)?.getTextSnippet(fieldInfo.value, fieldInfo)
        else if (t && fieldInfo) inputToOverrides.get(fieldInfo.fieldConstraint.field)?.getTextSnippet(t, fieldInfo) ?: fieldInfo.value ? inputToOverridesClasses.get(fieldInfo.fieldConstraint.field.type)?.getTextSnippet(fieldInfo.value, fieldInfo) : null
        else null
    }

    static <Z extends GormEntity<Z>> String formInputValue(Z t, FieldInfo fieldInfo) {
        if (!t && fieldInfo && fieldInfo.value) inputToOverridesClasses.get(fieldInfo.fieldConstraint.field.type)?.getValue(fieldInfo.value, fieldInfo)
        else if (t && fieldInfo) inputToOverrides.get(fieldInfo.fieldConstraint.field)?.getValue(t, fieldInfo) ?: fieldInfo.value ? inputToOverridesClasses.get(fieldInfo.fieldConstraint.field.type)?.getValue(fieldInfo.value, fieldInfo) : null
        else null
    }
}
