package taack.render

import grails.compiler.GrailsCompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo

import java.lang.reflect.Field

interface IShowOverrider<T extends GormEntity> {
    String getImagePreview(T t, FieldInfo fieldInfo)
    String getTextSnippet(T t, FieldInfo fieldInfo)
}

interface IFormInputOverrider<T extends GormEntity> extends IShowOverrider {
    String getValue(T t, FieldInfo fieldInfo)
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

    static<T extends GormEntity> void addInputToOverride(IFormInputOverrider inputOverrider, Class<T> aClass) {
        inputToOverridesClasses.put(aClass, inputOverrider)
    }

    static<T extends GormEntity> void addShowToOverride(IFormInputOverrider inputOverrider, Class<T> aClass) {
        showOverridesClasses.put(aClass, inputOverrider)
    }

    static boolean hasInputOverride(FieldInfo fieldInfo) {
        inputToOverrides.containsKey(fieldInfo.fieldConstraint.field) || inputToOverridesClasses.containsKey(fieldInfo.fieldConstraint.field.type)
    }

    static<T extends GormEntity> String formInputPreview(T t, FieldInfo fieldInfo) {
        inputToOverrides.get(fieldInfo.fieldConstraint.field)?.getImagePreview(t, fieldInfo) ?: inputToOverridesClasses.get(fieldInfo.fieldConstraint.field.type)?.getImagePreview(t, fieldInfo)
    }

    static<T extends GormEntity> String formInputSnippet(T t, FieldInfo fieldInfo) {
        inputToOverrides.get(fieldInfo.fieldConstraint.field)?.getTextSnippet(t, fieldInfo) ?: inputToOverridesClasses.get(fieldInfo.fieldConstraint.field.type)?.getTextSnippet(t, fieldInfo)
    }

    static<T extends GormEntity> String formInputValue(T t, FieldInfo fieldInfo) {
        inputToOverrides.get(fieldInfo.fieldConstraint.field)?.getValue(t, fieldInfo) ?: inputToOverridesClasses.get(fieldInfo.fieldConstraint.field.type)?.getValue(t, fieldInfo)
    }
}
