package taack.ui.dsl

import groovy.transform.CompileStatic
import taack.ui.dsl.printable.IUiPrintableVisitor
import taack.ui.dsl.printable.PrintableSpec

@CompileStatic
final class UiPrintableSpecifier {
    Closure closure
    Orientation orientation = Orientation.PORTRAIT

    /**
     * A4 page layout. Widths in mm size the body and the fixed-width columns of the PDF template;
     * diagram sizes, in CSS px, bound the box a diagram image is scaled into below the default 5cm header.
     */
    enum Orientation {
        PORTRAIT('210mm 297mm', '190mm', '60mm', '90mm', 672.0, 860.0),
        LANDSCAPE('297mm 210mm', '277mm', '87mm', '131mm', 980.0, 520.0)

        Orientation(String pageSize, String bodyWidth, String thirdWidth, String halfWidth, BigDecimal diagramMaxWidth, BigDecimal diagramMaxHeight) {
            this.pageSize = pageSize
            this.bodyWidth = bodyWidth
            this.thirdWidth = thirdWidth
            this.halfWidth = halfWidth
            this.diagramMaxWidth = diagramMaxWidth
            this.diagramMaxHeight = diagramMaxHeight
        }

        final String pageSize
        final String bodyWidth
        final String thirdWidth
        final String halfWidth
        final BigDecimal diagramMaxWidth
        final BigDecimal diagramMaxHeight
    }

    UiPrintableSpecifier ui(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PrintableSpec) Closure closure) {
        this.closure = closure
        this
    }

    UiPrintableSpecifier ui(Orientation orientation, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PrintableSpec) Closure closure) {
        this.orientation = orientation
        ui(closure)
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