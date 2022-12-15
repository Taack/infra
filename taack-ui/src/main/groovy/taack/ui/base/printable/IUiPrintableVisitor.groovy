package taack.ui.base.printable

import taack.ui.base.UiShowSpecifier
import taack.ui.base.UiTableSpecifier
import taack.ui.base.block.BlockSpec

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