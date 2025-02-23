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

    void filterAction(final String i18n = null, final MethodClosure action) {
        filterVisitor.visitFilterAction(i18n, action, ButtonStyle.SECONDARY)
    }
}
