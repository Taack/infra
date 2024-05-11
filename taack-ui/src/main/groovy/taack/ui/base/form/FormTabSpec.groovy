package taack.ui.base.form

import groovy.transform.CompileStatic

@CompileStatic
final class FormTabSpec {

    final IUiFormVisitor formVisitor

    FormTabSpec(IUiFormVisitor formVisitor) {
        this.formVisitor = formVisitor
    }

    /**
     * Add a tabulation to a section tabs. Can only be children of {@link FormSpec#tabs(groovy.lang.Closure)}.
     *
     * @param sectionName the label in the tab
     * @param closure describe the content of the tabulation
     */
    void tab(String sectionName,
             @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FormSpec) Closure closure) {
        formVisitor.visitFormTab(sectionName)
        closure.delegate = this
        closure.call()
        formVisitor.visitFormTabEnd()
    }

}
