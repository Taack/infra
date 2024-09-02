package taack.ui.dump.html.style

class DisplayNone implements IStyleDescriptor {
    @Override
    String getStyleOutput() {
        return 'display: none;'
    }

    @Override
    String getClasses() {
        return null
    }
}
