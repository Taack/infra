package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.base.block.BlockSpec
import taack.ui.base.form.FormSpec
import taack.ui.dump.html.base.HTMLDiv
import taack.ui.dump.html.base.IHTMLElement
import taack.ui.dump.html.base.TaackTag
import taack.ui.dump.html.theme.ThemeMode
import taack.ui.dump.html.theme.ThemeSize

@CompileStatic
final class BootstrapBlock implements IBlockTheme {

    final ThemeMode themeMode
    final ThemeSize themeSize

    BootstrapBlock(ThemeMode themeMode, ThemeSize themeSize) {
        this.themeMode = themeMode
        this.themeSize = themeSize
    }

    @Override
    IHTMLElement block(IHTMLElement topElement, String blockId) {
        topElement.addChildren(new HTMLDiv().builder.addClasses('taackBlock').setTaackTag(TaackTag.BLOCK).putAttribute('blockId', blockId).build())
        topElement.children.first()
    }

    @Override
    IHTMLElement blockAjax(IHTMLElement topElement, String blockId) {
        return null
    }

    @Override
    IHTMLElement innerBlock(IHTMLElement topElement, String i18n, String blockId, BlockSpec.Width width) {
        return null
    }

    @Override
    IHTMLElement annoBlock(IHTMLElement topElement, BlockSpec.Width width) {
        return null
    }

    @Override
    IHTMLElement innerModal(IHTMLElement topElement, String blockId) {
        return null
    }

    @Override
    IHTMLElement blockTabs(IHTMLElement topElement, int tabIds, List<String> names, FormSpec.Width width) {
        return null
    }

    @Override
    IHTMLElement blockTab(IHTMLElement topElement, int occ) {
        return null
    }
}
