package taack.ui.dsl.block

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.dsl.menu.MenuSpec

// TODO: try to remove ajaxBlock
// TODO: try to remove modal first param
// TODO: to improve completion, isolate ajaxTab from BlockSpec
// TODO: Turns ajaxBlock first parameter to be optional
/**
 * {@link taack.ui.dsl.UiBlockSpecifier#ui(groovy.lang.Closure)} delegated class.
 *
 * <p>This class allows to draw the layout of a page, or to update part of a page after an ajax call.
 *
 * <p>Each block can contains many graphical elements, but it is better to have one graphical
 * element (show, form, table, tableFilter ...) per block (modal, ajaxBlock ...)
 */
@CompileStatic
class BlockLayoutSpec extends BlockLeafSpec {

    BlockLayoutSpec(final IUiBlockVisitor blockVisitor) {
        super(blockVisitor)
    }

    /**
     * invisible blocks that enable complex layout. Can be nested.
     *
     * @param width
     * @param closure
     */
    void col(final BlockSpec.Width width = BlockSpec.Width.HALF, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockLayoutSpec) final Closure closure) {
        boolean doRender = blockVisitor.doRenderElement()
        if (doRender) blockVisitor.visitCol(width)
        closure.delegate = this
        closure.call()
        counter++
        if (doRender) blockVisitor.visitColEnd()
    }

    /**
     * invisible blocks that enable complex layout. Can be nested.
     *
     * @param width
     * @param closure
     */
    void row(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockLayoutSpec) final Closure closure) {
        boolean doRender = blockVisitor.doRenderElement()
        if (doRender) blockVisitor.visitRow()
        closure.delegate = this
        closure.call()
        counter++
        if (doRender) blockVisitor.visitRowEnd()
    }
}
