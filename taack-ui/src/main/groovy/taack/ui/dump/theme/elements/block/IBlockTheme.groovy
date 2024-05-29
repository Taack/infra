package taack.ui.dump.theme.elements.block

import groovy.transform.CompileStatic
import taack.ui.dump.theme.elements.base.IHTMLElement

@CompileStatic
trait IBlockTheme implements IHTMLElement {

    abstract IHTMLElement block(IHTMLElement topElement, String blockId)
    abstract IHTMLElement blockAjax(IHTMLElement topElement, String blockId)
    abstract IHTMLElement innerBlock(IHTMLElement topElement, String blockId)
    abstract IHTMLElement innerModal(IHTMLElement topElement, String blockId)

}