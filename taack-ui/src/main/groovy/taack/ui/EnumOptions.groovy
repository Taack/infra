package taack.ui

import groovy.transform.CompileStatic

@CompileStatic
final class EnumOption implements IEnumOption {

    final String key
    final String value
    final String asset
    final Boolean section

    EnumOption(String key, String value, String asset, Boolean isSection) {
        this.key = key
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

    final IEnumOption[] options
    final String paramKey

    EnumOptions(IEnumOption[] options, String paramKey) {
        this.options = options
        this.paramKey = paramKey
    }
}
