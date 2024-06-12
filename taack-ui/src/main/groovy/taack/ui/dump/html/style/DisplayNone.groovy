package taack.ui.dump.html.style

class DisplayNone implements IStyleDescriptor {
    @Override
    String getOutput() {
        return 'display: none;'
    }
}
