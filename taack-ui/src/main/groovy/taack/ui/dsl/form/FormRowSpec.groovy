package taack.ui.dsl.form


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

    /**
     * Add a section to enclose fields to display. Can be nested.
     *
     * @param sectionName the label of the section
     * @param width its relative width
     * @param closure Description of the content of this section
     */
    void section(String sectionName, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FormRowSpec) Closure closure) {
        formVisitor.visitFormSection(sectionName)
        closure.delegate = new FormRowSpec(formVisitor)
        closure.call()
        formVisitor.visitFormSectionEnd()
    }
}
