package taack.ui.dump.html.style

class DisplayBlock implements IStyleDescriptor {
    @Override
    String getStyleOutput() {
        return 'display: block;'
    }

    @Override
    String getClasses() {
        return null
    }
}
