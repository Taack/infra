package taack.ui.base.form


import groovy.transform.CompileStatic

@CompileStatic
class FormRowSpec extends FormAjaxFieldSpec {

    FormRowSpec(IUiFormVisitor formVisitor) {
        super(formVisitor)
    }

    void row(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FormColSpec) final Closure closure) {
        formVisitor.visitRow()
        closure.delegate = new FormColSpec(formVisitor)
        closure.call()
        formVisitor.visitRowEnd()
    }

}
