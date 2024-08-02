package taack.ui.dump.html.script

class CheckboxDisableIsZero implements IJavascriptDescriptor {

    @Override
    String getOutput() {
        return "this.previousElementSibling.name=this.checked?'':this.name;"
    }
}
