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
final class BlockSpec extends BlockTabSpec {
    final MenuSpec menuSpec

    int counter = 0
    int ajaxCounter = 0

    BlockSpec(final IUiBlockVisitor blockVisitor) {
        this.blockVisitor = blockVisitor
        this.menuSpec = new MenuSpec(blockVisitor)
    }

    /**
     * Helper method allowing to split block creation. See {@link #inline(groovy.lang.Closure)} to
     * inject this block into another block.
     *
     * @param closure closure that should benefit from IDE completion and static type checking
     * @return typed closure
     */
    static Closure<BlockSpec> buildBlockSpec(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        closure
    }

    /**
     * Allow to inject a block part that has been created elsewhere.
     * See {@link #buildBlockSpec(groovy.lang.Closure)}
     *
     * @param blockSpecClosure
     */
    void inline(final Closure<BlockSpec> blockSpecClosure) {
        blockSpecClosure.delegate = this
        blockSpecClosure.call()
    }

    enum Width {
        FLEX('pure-u-1', 'pure-u-1', 'col'),
        MAX('pure-u-1', 'pure-u-1', 'col-12'),
        THREE_QUARTER('pure-u-1 pure-u-md-3-4', 'pure-u-3-4', 'col-12 col-md-9'),
        TWO_THIRD('pure-u-1 pure-u-md-2-3', 'pure-u-2-3', 'col-12 col-md-8'),
        HALF('pure-u-1 pure-u-md-1-2', 'pure-u-1-2', 'col-12 col-md-6'),
        THIRD('pure-u-1 pure-u-md-1-3', 'pure-u-1-3', 'col-12 col-sm-6 col-md-4'),
        QUARTER('pure-u-1 pure-u-md-1-4', 'pure-u-1-4', 'col-12 col-md-3')

        Width(final String css, final String pdfCss, final String bootstrapCss) {
            this.css = css
            this.pdfCss = pdfCss
            this.bootstrapCss = bootstrapCss
        }

        final String css
        final String pdfCss
        final String bootstrapCss
    }

    /**
     * Ajax block must be children of ajaxBlock.
     *
     * Mandatory invisible block that enable ajax.
     *
     * @param id uniq id in the page.
     * @param visitAjax set to false to process block like non ajax block
     * @param closure description of the user interface
     */
    void ajaxBlock(final String id = null, Boolean visitAjax = true, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockLayoutSpec) final Closure closure) {
        if (visitAjax && id) blockVisitor.setExplicitAjaxBlockId(id)
        boolean doRender = blockVisitor.doRenderElement(id)
        simpleLog("ajaxBlock $id $doRender")
        if (!id || doRender) {
            closure.delegate = this
            closure.call()
            counter++
        }
        if (visitAjax && id) blockVisitor.setExplicitAjaxBlockId(null)
    }

    /**
     * Pop a modal. Nested blocks will be displayed inside this modal.
     *
     * @param firstPass (optional) if true create a new modal, if false, replace the content of the top modal.
     * @param closure content of the modal
     */
    void modal(final boolean reloadWhenClose = false, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        blockVisitor.visitModal(reloadWhenClose)
        closure.delegate = this
        closure.call()
        counter++
        blockVisitor.visitModalEnd()
    }

}
