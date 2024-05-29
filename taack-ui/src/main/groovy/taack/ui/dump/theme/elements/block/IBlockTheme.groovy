package taack.ui.dump.theme.elements.block

import groovy.transform.CompileStatic
import taack.ui.base.block.BlockSpec
import taack.ui.base.form.FormSpec
import taack.ui.dump.theme.elements.base.IHTMLElement

@CompileStatic
trait IBlockTheme implements IHTMLElement {

    abstract IHTMLElement block(IHTMLElement topElement, String blockId)

    abstract IHTMLElement blockAjax(IHTMLElement topElement, String blockId)

    abstract IHTMLElement innerBlock(IHTMLElement topElement, String i18n, String blockId, BlockSpec.Width width)

    abstract IHTMLElement annoBlock(IHTMLElement topElement, BlockSpec.Width width)

    abstract IHTMLElement innerModal(IHTMLElement topElement, String blockId)

    abstract IHTMLElement blockTabs(IHTMLElement topElement, int tabIds, List<String> names, FormSpec.Width width)

    abstract IHTMLElement blockTab(IHTMLElement topElement, int occ)

}