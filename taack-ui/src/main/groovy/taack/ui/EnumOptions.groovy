package taack.ui

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

import static taack.render.TaackUiService.tr

@CompileStatic
final class EnumOption implements IEnumOption {

    final String key
    final String value
    final String asset
    final Boolean section

    EnumOption(String key, String value, String asset = null, Boolean isSection = false) {
        this.key = key?.replace('"', '&quot;')?.replace('\'', '&#39;')?.replace('\n', '')?.replace('\r', '')
        this.value = value
        this.asset = asset
        this.section = isSection
    }

    @Override
    Boolean isSection() {
        return section
    }
}


@CompileStatic
final class EnumOptions implements IEnumOptions {
    private final static String ST_NAME = 'name'

    final IEnumOption[] options
    final IEnumOption[] currents
    final String paramKey

    static String translateEnumValue(String value) {
        String trLabel = "enum.value.$value"
        String i18n = tr(trLabel, null)
        return i18n != trLabel ? i18n : value
    }

    EnumOptions(IEnumOption[] options, String paramKey, EnumOption... currents) {
        this.currents = currents
        this.options = options
        this.paramKey = paramKey
    }

    EnumOptions(IEnumOption[] options, String paramKey, String... currents) {
        this.options = options

        if (currents && currents.size() > 0) {
            this.currents = new EnumOption[currents.size()]

            for (int i = 0; i < currents.size(); i++) {
                final String v = currents[i]
                final String name = v
                if (v) this.currents[i] = new EnumOption(v, name)
            }
        }

        this.paramKey = paramKey
    }

    EnumOptions(IEnumOption[] options, String paramKey, Enum... currents) {
        this.options = options

        if (currents && currents.size() > 0) {
            this.currents = new EnumOption[currents.size()]

            for (int i = 0; i < currents.size(); i++) {
                final Enum v = currents[i]
                if (v) {
                    final String name = v.hasProperty(ST_NAME) ? v.getAt(ST_NAME) : translateEnumValue(v.toString())
                    this.currents[i] = new EnumOption(v.name(), name)
                }
            }
        }

        this.paramKey = paramKey
    }

    EnumOptions(IEnumOption[] options, FieldInfo info) {
        Object v = info.value
        if (v) {
            Class type = info.fieldConstraint.field.type
            boolean isListOrSet = Collection.isAssignableFrom(type)
            if (isListOrSet) {
                final ParameterizedType parameterizedType = info.fieldConstraint.field.genericType as ParameterizedType
                final Type actualType = parameterizedType.actualTypeArguments.first()
                final Class actualClass = Class.forName(actualType.typeName)
                final boolean isEnumListOrSet = actualClass.isEnum()

                List currents = v as List
                this.currents = new EnumOption[currents.size()]
                for (int i = 0; i < currents.size(); i++) {
                    if (isEnumListOrSet) {
                        String name = currents[i].hasProperty(ST_NAME) ? currents[i].getAt(ST_NAME) : translateEnumValue(currents[i].toString())
                        this.currents[i] = new EnumOption((currents[i] as Enum).name(), name)
                    } else {
                        this.currents[i] = new EnumOption(currents[i].toString(), currents[i].toString())
                    }
                }
            } else {
                boolean isEnum = info.fieldConstraint.field.type.isEnum()
                if (isEnum) {
                    String name = v.hasProperty(ST_NAME) ? v.getAt(ST_NAME) : translateEnumValue(v.toString())
                    this.currents = [new EnumOption((v as Enum).name(), name)]
                } else {
                    this.currents = [new EnumOption(v.toString(), v.toString())]

                }
            }
        } else {
            this.currents = null
        }

        this.options = options
        this.paramKey = info.fieldName
    }

    EnumOptions(Class<? extends Enum> options, String paramKey, EnumOption... currents) {
        Enum[] values = options.invokeMethod('values', null) as Enum[]
        this.options = new EnumOption[values.size()]

        for (int i = 0; i < values.size(); i++) {
            final String v = values[i].name()
            final String name = values[i].hasProperty(ST_NAME) ? values[i].getAt(ST_NAME) : translateEnumValue(v)
            this.options[i] = new EnumOption(v, name)
        }
        this.currents = currents
        this.paramKey = paramKey
    }

    EnumOptions(Class<? extends Enum> options, String paramKey, Enum... currents) {
        Enum[] values = options.invokeMethod('values', null) as Enum[]
        this.options = new EnumOption[values.size()]

        for (int i = 0; i < values.size(); i++) {
            final String v = values[i].name()
            final String name = values[i].hasProperty(ST_NAME) ? values[i].getAt(ST_NAME) : translateEnumValue(v)
            this.options[i] = new EnumOption(v.toString(), name)
        }
        if (currents && currents.size() > 0) {
            this.currents = new EnumOption[currents.size()]

            for (int i = 0; i < currents.size(); i++) {
                final Enum v = currents[i]
                if (v) {
                    final String name = v.hasProperty(ST_NAME) ? v.getAt(ST_NAME) : translateEnumValue(v.toString())
                    this.currents[i] = new EnumOption(v.name(), name)
                }
            }
        }

        this.paramKey = paramKey
    }

    EnumOptions(Class<? extends Enum> options, String paramKey, String... currents) {
        Enum[] values = options.invokeMethod('values', null) as Enum[]
        this.options = new EnumOption[values.size()]

        for (int i = 0; i < values.size(); i++) {
            final String v = values[i].name()
            final String name = values[i].hasProperty(ST_NAME) ? values[i].getAt(ST_NAME) : translateEnumValue(v)
            this.options[i] = new EnumOption(v, name)
        }

        if (currents && currents.size() > 0) {
            this.currents = new EnumOption[currents.size()]

            for (int i = 0; i < currents.size(); i++) {
                final String v = currents[i]
                final String name = v
                if (v) this.currents[i] = new EnumOption(v, name)
            }
        }

        this.paramKey = paramKey
    }

    EnumOptions(Enum[] values, String paramKey, Enum... currents) {
        this.options = new EnumOption[values.size()]

        for (int i = 0; i < values.size(); i++) {
            final String v = values[i]
            final String name = v.hasProperty(ST_NAME) ? values[i].getAt(ST_NAME) : translateEnumValue(v)
            this.options[i] = new EnumOption(v, name)
        }

        if (currents && currents.size() > 0) {
            this.currents = new EnumOption[currents.size()]

            for (int i = 0; i < currents.size(); i++) {
                final String v = currents[i]
                final String name = v.hasProperty(ST_NAME) ? values[i].getAt(ST_NAME) : translateEnumValue(v)
                this.currents[i] = new EnumOption(v, name)
            }
        }
        this.paramKey = paramKey
    }

}
