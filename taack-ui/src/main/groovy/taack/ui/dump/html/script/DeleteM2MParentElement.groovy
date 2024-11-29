package taack.ui.dump.html.script

class DeleteM2MParentElement implements IJavascriptDescriptor {

    @Override
    String getOutput() {
        return "if (this.parentElement.classList.contains('M2MParent')) this.parentElement.remove();"
    }
}
