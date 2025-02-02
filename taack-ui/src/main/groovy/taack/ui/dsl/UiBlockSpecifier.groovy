package taack.ui.dsl

import groovy.transform.CompileStatic
import taack.ui.dsl.block.BlockLeafSpec
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.block.IUiBlockVisitor

/**
 * Class assembling graphical elements to display. All graphical elements are displayed in a block.
 * A block is drawn with {@link taack.render.TaackUiService#show(UiBlockSpecifier)}
 * to the browser.
 *
 * <p>A simple code sample:
 * <pre>{@code
 *  // showUser is a regular Grails action
 *  def showUser(User u) {
 *      taackUiSimpleService.show(<b>new UiBlockSpecifier()</b>.ui {
 *          modal {
 *              ajaxBlock 'showUser', {
 *                  show u.username, crewUiService.buildUserShow(u), BlockSpec.Width.MAX
 *              }
 *          }
 *      })
 *  }
 * }</pre>
 * <p>See {@link BlockSpec} for possible associations
 */
@CompileStatic
final class UiBlockSpecifier {
    Closure closure

    static void simpleLog(String toPrint) {
        if (BlockLeafSpec.debug) println(UiBlockSpecifier.simpleName + '::' + toPrint)
    }

    /**
     * Describe the block to display (see {@link BlockSpec})
     *
     * @param closure the description of the elements to display
     * @return itself
     */
    UiBlockSpecifier ui(final @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) Closure closure) {
        this.closure = closure
        this
    }

    /**
     * Visit the block description with a {@link IUiBlockVisitor}
     *
     * @param blockVisitor the visitor
     */
    void visitBlock(final IUiBlockVisitor blockVisitor) {
        if (blockVisitor && closure) {
            boolean doRender = blockVisitor.doRenderElement()
            simpleLog("visitBlock $doRender")
            if (doRender) blockVisitor.visitBlock()
            closure.delegate = new BlockSpec(blockVisitor)
            closure.call()
            if (doRender) blockVisitor.visitBlockEnd()
        }
    }
}