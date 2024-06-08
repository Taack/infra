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
    void tabLabel(String sectionName,
                  @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FormSpec) Closure closure) {
        formVisitor.visitFormTab(sectionName)
        closure.delegate = new FormSpec(formVisitor)
        closure.call()
        formVisitor.visitFormTabEnd()
    }

}
