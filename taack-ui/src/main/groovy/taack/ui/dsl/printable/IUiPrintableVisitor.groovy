package taack.ui.dsl.printable

import taack.ui.dsl.UiShowSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.block.BlockSpec

interface IUiPrintableVisitor {

    void visitBlock()

    void visitBlockEnd()

    void visitAnonymousBlock(BlockSpec.Width width)

    void visitAnonymousBlockEnd()

    void visitShow(UiShowSpecifier uiShowSpecifier, BlockSpec.Width width)

    void visitTable(UiTableSpecifier uiTableSpecifier, BlockSpec.Width width)

    void visitCustom(String html, BlockSpec.Width width)

    void visitPrintableHeaderEnd()

    void visitPrintableFooter()

    void visitPrintableFooterEnd()

    void visitInnerBlockEnd()

    void visitInnerBlock(BlockSpec.Width width)

    void visitPrintableBody()

    void visitPrintableBodyEnd()

    void visitPrintableHeader(String height)

    void visitPrintableHeaderLeft(String height)

    void visitPrintableHeaderLeftEnd()

    void visitPrintableHeaderRight(String right)

    void visitPrintableHeaderRightEnd()
}