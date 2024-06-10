package taack.ui.base.form


import groovy.transform.CompileStatic

@CompileStatic
class FormColSpec extends FormAjaxFieldSpec {

    /**
     * Add a section to enclose fields to display. Can be nested.
     *
     * @param sectionName the label of the section
     * @param width its relative width
     * @param closure Description of the content of this section
     */
    FormColSpec(IUiFormVisitor formVisitor) {
        super(formVisitor)
    }

    void col(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FormRowSpec) final Closure closure) {
        formVisitor.visitCol()
        closure.delegate = new FormRowSpec(formVisitor)
        closure.call()
        formVisitor.visitColEnd()
    }

}
