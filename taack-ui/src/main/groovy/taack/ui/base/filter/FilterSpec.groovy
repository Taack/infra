package taack.ui.base.filter

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure

@CompileStatic
final class FilterSpec extends SectionSpec {
    FilterSpec(final IUiFilterVisitor filterVisitor) {
        super(filterVisitor)
    }

    void hiddenId(final Long id) {
        filterVisitor.visitHiddenId(id)
    }

    void section(final String i18n,
                 @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = SectionSpec) final Closure closure) {
        filterVisitor.visitSection(i18n)
        closure.delegate = this
        closure.call()
        filterVisitor.visitSectionEnd()
    }

    void filterAction(final String i18n, final MethodClosure action) {
        filterVisitor.visitFilterAction(i18n, action)
    }
}
