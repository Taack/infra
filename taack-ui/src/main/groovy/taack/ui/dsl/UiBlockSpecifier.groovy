package taack.ui.dsl

import groovy.transform.CompileStatic
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
            println "AUOAUOAUOAUO +++ ${blockVisitor.doDisplay(null)}"
            if (blockVisitor.doDisplay(null)) blockVisitor.visitBlock()
            closure.delegate = new BlockSpec(blockVisitor)
            closure.call()
            if (blockVisitor.doDisplay(null)) blockVisitor.visitBlockEnd()
            println "AUOAUOAUOAUO --- ${blockVisitor.doDisplay(null)}"
        }
    }
}