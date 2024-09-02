package taack.ui.dump.html.style

class DisplayInlineBlock implements IStyleDescriptor {
    @Override
    String getStyleOutput() {
        return 'display: inline-block;'
    }

    @Override
    String getClasses() {
        return null
    }
}
