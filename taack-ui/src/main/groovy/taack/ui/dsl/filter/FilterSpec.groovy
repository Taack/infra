package taack.ui.dsl.filter

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.dump.html.element.ButtonStyle

@CompileStatic
final class FilterSpec extends FilterCommon {

    FilterSpec(final IUiFilterVisitor filterVisitor) {
        super(filterVisitor)
    }

    void hiddenId(final Long id) {
        filterVisitor.visitHiddenId(id)
    }

    void section(final String i18n = null,
                 @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = SectionSpec) final Closure closure) {
        if (i18n) filterVisitor.visitSection(i18n)
        closure.delegate = new SectionSpec(filterVisitor)
        closure.call()
        if (i18n) filterVisitor.visitSectionEnd()
    }

    void filterAction(final String i18n, final MethodClosure action) {
        filterVisitor.visitFilterAction(i18n, action, ButtonStyle.SECONDARY)
    }
}
