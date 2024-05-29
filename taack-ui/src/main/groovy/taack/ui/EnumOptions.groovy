package taack.ui

import groovy.transform.CompileStatic
import taack.ui.dump.theme.elements.base.IHTMLElement

@CompileStatic
final class EnumOption implements IEnumOption {

    final String key
    final String value
    final String asset
    final Boolean section

    EnumOption(String key, String value, String asset, Boolean isSection) {
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

    EnumOptions(IEnumOption[] options, String paramKey, EnumOption... currents) {
        this.currents = currents
        this.options = options
        this.paramKey = paramKey
    }

    EnumOptions(Class<? extends Enum> options, String paramKey, EnumOption... currents) {
        Object[] values = options.invokeMethod('values', null) as Object[]
        this.options = new EnumOption[values.size()]

        for (int i = 0; i < values.size(); i++) {
            final String v = values[i]
            final String name = v.hasProperty(ST_NAME) ? values[i].getAt(ST_NAME) : v
            this.options[i] = new EnumOption(v, name, null, false)
        }
        this.currents = currents
        this.paramKey = paramKey
    }

    EnumOptions(Class<? extends Enum> options, String paramKey, Enum... currents) {
        String[] values = options.invokeMethod('values', null) as String[]
        this.options = new EnumOption[values.size()]

        for (int i = 0; i < values.size(); i++) {
            final String v = values[i]
            final String name = v.hasProperty(ST_NAME) ? values[i].getAt(ST_NAME) : v
            this.options[i] = new EnumOption(v, name, null, false)
        }

        if (currents && currents.size() > 0) {
            this.currents = new EnumOption[currents.size()]

            for (int i = 0; i < currents.size(); i++) {
                final String v = currents[i]
                if (v) {
                    final String name = v?.hasProperty(ST_NAME) ? values[i]?.getAt(ST_NAME) : v
                    this.currents[i] = new EnumOption(v, name, null, false)
                }
            }
        }

        this.paramKey = paramKey
    }

    EnumOptions(Class<? extends Enum> options, String paramKey, String... currents) {
        String[] values = options.invokeMethod('values', null) as String[]
        this.options = new EnumOption[values.size()]

        for (int i = 0; i < values.size(); i++) {
            final String v = values[i]
            final String name = v.hasProperty(ST_NAME) ? values[i].getAt(ST_NAME) : v
            this.options[i] = new EnumOption(v, name, null, false)
        }

        if (currents && currents.size() > 0) {
            this.currents = new EnumOption[currents.size()]

            for (int i = 0; i < currents.size(); i++) {
                final String v = currents[i]
                final String name = v
                if (v) this.currents[i] = new EnumOption(v, name, null, false)
            }
        }

        this.paramKey = paramKey
    }

    EnumOptions(Enum[] values, String paramKey, Enum... currents) {
        this.options = new EnumOption[values.size()]

        for (int i = 0; i < values.size(); i++) {
            final String v = values[i]
            final String name = v.hasProperty(ST_NAME) ? values[i].getAt(ST_NAME) : v
            this.options[i] = new EnumOption(v, name, null, false)
        }

        if (currents && currents.size() > 0) {
            this.currents = new EnumOption[currents.size()]

            for (int i = 0; i < currents.size(); i++) {
                final String v = currents[i]
                final String name = v.hasProperty(ST_NAME) ? values[i].getAt(ST_NAME) : v
                this.currents[i] = new EnumOption(v, name, null, false)
            }
        }
        this.paramKey = paramKey
    }

}
