package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.form.FormSpec
import taack.ui.dump.html.element.HTMLDiv
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.layout.BootstrapLayout
import taack.ui.dump.html.theme.ThemeMode
import taack.ui.dump.html.theme.ThemeSize

@CompileStatic
final class BootstrapBlock extends BootstrapLayout implements IBlockTheme {

    final ThemeMode themeMode
    final ThemeSize themeSize

    BootstrapBlock(ThemeMode themeMode, ThemeSize themeSize) {
        this.themeMode = themeMode
        this.themeSize = themeSize
    }

    @Override
    IHTMLElement block(String blockId) {
        new HTMLDiv().builder.addClasses('taackBlock', 'container-fluid').setTaackTag(TaackTag.BLOCK).putAttribute('blockId', blockId).build()
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
