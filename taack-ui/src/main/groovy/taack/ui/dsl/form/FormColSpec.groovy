package taack.ui.dsl.form


import groovy.transform.CompileStatic
import taack.ui.dsl.block.BlockSpec.Width

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

    void col(Width width = Width.FLEX, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FormRowSpec) final Closure closure) {
        formVisitor.visitCol(width)
        closure.delegate = new FormRowSpec(formVisitor)
        closure.call()
        formVisitor.visitColEnd()
    }

}
