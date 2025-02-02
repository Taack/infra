package taack.ui.dsl.block

import groovy.transform.CompileStatic

/**
 * {@link taack.ui.dsl.UiBlockSpecifier#ui(groovy.lang.Closure)} delegated class.
 *
 * <p>This class allows to draw the layout of a page, or to update part of a page after an ajax call.
 *
 * <p>Each block can contains many graphical elements, but it is better to have one graphical
 * element (show, form, table, tableFilter ...) per block (modal, ajaxBlock ...)
 */
@CompileStatic
class BlockTabSpec extends BlockLayoutSpec {

    /**
     *
     * @param i18n label
     * @param closure content of the tabulation.
     */
    void tab(final String i18n, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockLayoutSpec) final Closure closure) {
        String tabIndex = blockVisitor.parameterMap?['tabIndex']
        String ajaxBlockId = blockVisitor.parameterMap?['ajaxBlockId']
        if (tabIndex != null && ajaxBlockId == null) blockVisitor.setRenderTab(true)
        boolean doRender = blockVisitor.doRenderElement()
        if (debug) println('BlockTabSpec::' + doRender)
        if (doRender) blockVisitor.visitBlockTab(i18n)
        if ((tabIndex == null && tabCounter == 0) || (tabIndex != null && tabCounter.toString() == tabIndex)) {
            closure.delegate = this
            closure.call()
        }
        counter++
        tabCounter ++
        if (doRender) blockVisitor.visitBlockTabEnd()
        blockVisitor.setRenderTab(false)
    }

}
