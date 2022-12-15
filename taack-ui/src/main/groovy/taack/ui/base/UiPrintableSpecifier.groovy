package taack.ui.base

import groovy.transform.CompileStatic
import taack.ui.base.printable.IUiPrintableVisitor
import taack.ui.base.printable.PrintableSpec

@CompileStatic
final class UiPrintableSpecifier {
    Closure closure

    UiPrintableSpecifier ui(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PrintableSpec) Closure closure) {
        this.closure = closure
        this
    }

    void visitPrintableBlock(final IUiPrintableVisitor printableVisitor) {
        if (printableVisitor && closure) {
            printableVisitor.visitBlock()
            closure.delegate = new PrintableSpec(printableVisitor)
            closure.call()
            printableVisitor.visitBlockEnd()
        }
    }
}