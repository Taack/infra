package taack.ui.dump.html.script

class DeleteSiblingInputContent implements IJavascriptDescriptor {

    @Override
    String getOutput() {
        return "list=this.parentElement.getElementsByTagName('input');for (let item of list) item.value='';"
    }
}
