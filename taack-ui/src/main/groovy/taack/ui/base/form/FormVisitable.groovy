package taack.ui.base.form

import groovy.transform.CompileStatic

@CompileStatic
class FormVisitable {
    final IUiFormVisitor formVisitor

    FormVisitable(IUiFormVisitor formVisitor) {
        this.formVisitor = formVisitor
    }
}
