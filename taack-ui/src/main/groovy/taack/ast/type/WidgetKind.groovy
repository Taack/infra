package taack.ast.type

/**
 * List all constraints widget allowed values.
 */
enum WidgetKind {
    FILE_PATH("filePath"),
    TEXTAREA("textarea"),
    PASSWD("passwd"),
    AJAX("ajax"),
    MARKDOWN("markdown")

    WidgetKind(final String name) {
        this.name = name
    }

    final String name
}