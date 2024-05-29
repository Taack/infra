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
    final IEnumOption current
    final String paramKey

    EnumOptions(IEnumOption[] options, String paramKey, EnumOption current) {
        this.current = current
        this.options = options
        this.paramKey = paramKey
    }

    EnumOptions(Class<? extends Enum> options, String paramKey) {
        String[] values = options.invokeMethod('values', null) as String[]
        this.options = new EnumOption[values.size()]

        for (int i = 0; i < values.size(); i++) {
            final String v = values[i]
            final String name = v.hasProperty(ST_NAME) ? values[i].getAt(ST_NAME) : v
            this.options[i] = new EnumOption(v, name, null, false)
        }

        this.paramKey = paramKey
    }

    @Override
    String getKey() {
        return current.key
    }

    @Override
    String getValue() {
        return current.value
    }

    @Override
    String getAsset() {
        return current.asset
    }

    @Override
    Boolean isSection() {
        return current.section
    }

}
