package taack.ui.dump.html.script

class DeleteM2MParentElement implements IJavascriptDescriptor {

    @Override
    String getOutput() {
        return """
if (this.parentElement.classList.contains('M2MParent')) { 
    let m2mRoot = this.parentElement.parentElement;
    this.parentElement.remove(); 
    if (m2mRoot.getElementsByClassName('M2MParent').length === 0) {
        let i = m2mRoot.querySelector('div.M2MToDuplicate>input[type=\\'hidden\\']');
        i.name = i.getAttribute('attr-name');
    }
}"""
    }
}
