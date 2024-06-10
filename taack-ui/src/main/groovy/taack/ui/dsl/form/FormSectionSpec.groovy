package taack.ui.dsl.form


import groovy.transform.CompileStatic

@CompileStatic
class FormSectionSpec extends FormAjaxFieldSpec {


    FormSectionSpec(IUiFormVisitor formVisitor) {
        super(formVisitor)
    }

    /**
     * Add a section to enclose fields to display. Can be nested.
     *
     * @param sectionName the label of the section
     * @param width its relative width
     * @param closure Description of the content of this section
     */
    void section(String sectionName, FormSpec.Width width = FormSpec.Width.DEFAULT_WIDTH,
                 @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FormRowSpec) Closure closure) {
        formVisitor.visitFormSection(sectionName, width)
        closure.delegate = new FormRowSpec(formVisitor)
        closure.call()
        formVisitor.visitFormSectionEnd()
    }

}
