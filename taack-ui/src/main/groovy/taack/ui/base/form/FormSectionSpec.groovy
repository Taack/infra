package taack.ui.base.form

import groovy.transform.CompileStatic

@CompileStatic
class FormSectionSpec extends FormAjaxFieldSpec {

    FormSectionSpec(IUiFormVisitor formVisitor) {
        super(formVisitor)
    }

    void col(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FormSectionSpec) final Closure closure) {
        formVisitor.visitCol()
        closure.delegate = this
        closure.call()
        formVisitor.visitColEnd()
    }
}
