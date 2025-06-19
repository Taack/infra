package taack.ast.type

/**
 * List all constraints widget allowed values.
 */
enum WidgetKind {
    FILE_PATH('filePath'),
    TEXTAREA('textarea'),
    PASSWD('passwd'),
    AJAX('ajax'),
    MARKDOWN('markdown'),
    ASCIIDOC('asciidoc'),
    DATETIME('datetime')

    WidgetKind(final String name) {
        this.name = name
    }

    final String name
}