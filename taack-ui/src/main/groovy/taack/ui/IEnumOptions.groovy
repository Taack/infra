package taack.ui

interface IEnumOptions extends IEnumOption {
    IEnumOption[] getOptions()
    String getParamKey()
}
