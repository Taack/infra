package taack.ui.dsl.printable

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.UiShowSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.menu.MenuSpec

@CompileStatic
class PrintableSpec {
    final IUiPrintableVisitor printableVisitor

    PrintableSpec(final IUiPrintableVisitor printableVisitor) {
        this.printableVisitor = printableVisitor
    }

    void printableHeader(final String height = null, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PrintableSpec) final Closure closure) {
        printableVisitor.visitPrintableHeader(height)
        closure.delegate = this
        closure.call()
        printableVisitor.visitPrintableHeaderEnd()
    }

    void printableHeaderLeft(final String height = null, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PrintableSpec) final Closure closure) {
        printableVisitor.visitPrintableHeaderLeft(height)
        closure.delegate = this
        closure.call()
        printableVisitor.visitPrintableHeaderLeftEnd()
    }

    void printableHeaderRight(final String height = null, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PrintableSpec) final Closure closure) {
        printableVisitor.visitPrintableHeaderRight(height)
        closure.delegate = this
        closure.call()
        printableVisitor.visitPrintableHeaderRightEnd()
    }

    void printableFooter(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PrintableSpec) final Closure closure) {
        printableVisitor.visitPrintableFooter()
        closure.delegate = this
        closure.call()
        printableVisitor.visitPrintableFooterEnd()
    }

    void printableBody(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PrintableSpec) final Closure closure) {
        printableVisitor.visitPrintableBody()
        closure.delegate = this
        closure.call()
        printableVisitor.visitPrintableBodyEnd()
    }

    void anonymousBlock(final BlockSpec.Width width, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PrintableSpec) final Closure closure) {
        printableVisitor.visitAnonymousBlock(width)
        closure.delegate = this
        closure.call()
        printableVisitor.visitAnonymousBlockEnd()
    }

    void show(final UiShowSpecifier showSpecifier, final BlockSpec.Width width) {
        printableVisitor.visitShow(showSpecifier, width)
    }

    void table(final UiTableSpecifier tableSpecifier, final BlockSpec.Width width,
               @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        printableVisitor.visitTable(tableSpecifier, width)
    }

    void diagram(final UiDiagramSpecifier diagramSpecifier, final BlockSpec.Width width,
                 @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        printableVisitor.visitDiagram(diagramSpecifier, width)
    }

    void custom(final String html, final BlockSpec.Width width,
                @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        printableVisitor.visitCustom(html, width)
    }
}
