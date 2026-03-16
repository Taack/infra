package taack.ui.dsl.form

import groovy.transform.CompileStatic

/**
 * Used to get tab names from tabLabel() calls without running the inner closures.
 * Extends FormTabSpec so the closure's @DelegatesTo works.
 */
@CompileStatic
final class FormTabNameCollector extends FormTabSpec {

    final List<String> tabNames

    FormTabNameCollector(List<String> tabNames) {
        super(new UiFormVisitorImpl())
        this.tabNames = tabNames
    }

    @Override
    void tabLabel(String sectionName, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FormRowSpec) Closure closure) {
        tabNames << sectionName
        // Intentionally NOT calling closure, this just collects name
    }
}
