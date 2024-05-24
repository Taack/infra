package taack.ui.dump.theme

class RetTheme {
    final String attributes
    final String classes

    RetTheme(String classes, String attributes) {
        this.classes = classes
        this.attributes = attributes
    }
}

interface IFormTheme {
    String labelWithInput(String i18n, String inputString)
    RetTheme inputTheme(boolean readOnly, boolean disabled)
    RetTheme inputCheckTheme(boolean readOnly, boolean disabled)
    RetTheme inputSelectTheme(boolean readOnly, boolean disabled)
}