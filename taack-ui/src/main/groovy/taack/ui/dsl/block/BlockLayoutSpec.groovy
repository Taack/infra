package taack.ui.dsl.block

import groovy.transform.CompileStatic
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

    static void simpleLog(String toPrint) {
        if (debug) println(BlockLayoutSpec.simpleName + '::' + toPrint)
    }

    static Closure<BlockLayoutSpec> buildBlockLayoutSpec(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockLayoutSpec) final Closure closure) {
        closure
    }

    /**
     * invisible blocks that enable complex layout. Can be nested.
     *
     * @param width
     * @param closure
     */
    void col(final BlockSpec.Width width = BlockSpec.Width.HALF, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockLayoutSpec) final Closure closure) {
        boolean doRender = blockVisitor.doRenderLayoutElement()
        simpleLog("col $doRender")
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
        boolean doRender = blockVisitor.doRenderLayoutElement()
        simpleLog("row $doRender")
        if (doRender) blockVisitor.visitRow()
        closure.delegate = this
        closure.call()
        counter++
        if (doRender) blockVisitor.visitRowEnd()
    }

    void tabs(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockTabSpec) final Closure closure) {
        boolean doRender = blockVisitor.doRenderElement()
        simpleLog("tabs $doRender")
        if (doRender) blockVisitor.visitBlockTabs()
        closure.delegate = this
        closure.call()
        counter++
        if (doRender) blockVisitor.visitBlockTabsEnd()
    }

    void poke(Boolean update, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockTabSpec) final Closure closure) {
        blockVisitor.visitBlockPoke(update)
        closure.delegate = this
        closure.call()
        counter++
        blockVisitor.visitBlockPokeEnd()
    }

    void accordion(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockLayoutSpec) final Closure closure) {
        boolean doRender = blockVisitor.doRenderElement()
        simpleLog("accordion $doRender")
        if (doRender) blockVisitor.visitBlockAccordion()
        closure.delegate = this
        closure.call()
        counter++
        if (doRender) blockVisitor.visitBlockAccordionEnd()
    }

    void accordionItem(final String i18n, final boolean openByDefault = false,
                       @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockLayoutSpec) final Closure closure) {
        boolean doRender = blockVisitor.doRenderElement()
        simpleLog("accordionItem $i18n $doRender")
        if (doRender) blockVisitor.visitBlockAccordionItem(i18n, openByDefault)
        closure.delegate = this
        closure.call()
        counter++
        if (doRender) blockVisitor.visitBlockAccordionItemEnd()
    }

    void card(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure menuClosure,
              @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockLayoutSpec) final Closure contentClosure) {
        card(null, menuClosure, contentClosure)
    }

    void card(final String title = null,
              @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure menuClosure = null,
              @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockLayoutSpec) final Closure contentClosure) {
        String aId = theAjaxBlockId('card')
        boolean doRender = blockVisitor.doRenderLayoutElement()
        simpleLog("card $title $doRender")
        if (doRender) {
            blockVisitor.visitAjaxBlock(aId)
            blockVisitor.visitBlockCard(title, menuClosure != null)
            if (menuClosure) {
//                blockVisitor.visitMenuStart(MenuSpec.MenuMode.HORIZONTAL, null)
//                menuClosure.delegate = new MenuSpec(blockVisitor)
//                menuClosure.call()
//                blockVisitor.visitMenuStartEnd()
                processMenuBlock(aId, menuClosure)
            }
            blockVisitor.visitBlockCardBody()
        }
        contentClosure.delegate = this
        contentClosure.call()
        counter++
        if (doRender) {
            blockVisitor.visitBlockCardEnd()
            blockVisitor.visitAjaxBlockEnd()
        }
    }

    void scrollPanel(final String maxHeight, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockLayoutSpec) final Closure closure) {
        boolean doRender = blockVisitor.doRenderLayoutElement()
        simpleLog("scrollPanel $maxHeight $doRender")
        if (doRender) blockVisitor.visitBlockScrollPanel(maxHeight)
        closure.delegate = this
        closure.call()
        counter++
        if (doRender) blockVisitor.visitBlockScrollPanelEnd()
    }
}
