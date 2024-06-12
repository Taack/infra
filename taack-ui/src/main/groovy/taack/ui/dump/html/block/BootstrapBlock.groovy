package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.form.FormSpec
import taack.ui.dump.html.element.HTMLButton
import taack.ui.dump.html.element.HTMLDiv
import taack.ui.dump.html.element.HTMLNav
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

    static IHTMLElement blockHeader(IHTMLElement topElement) {
        topElement.addChildren(
                new HTMLNav().builder.addClasses('navbar', 'navbar-expand-md').setTaackTag(TaackTag.MENU_BLOCK).addChildren(
                        new HTMLDiv().builder.addClasses('container-fluid').setId('dropdownNav').addChildren(
                                new HTMLButton(null, '<span class="navbar-toggler-icon"></span>').builder
                                        .addClasses('navbar-toggler', 'navbar-dark')
                                        .putAttribute('data-bs-toggle', 'collapse')
                                        .putAttribute('data-bs-target', '#navbarSupportedContent')
                                        .putAttribute('aria-controls', 'navbarSupportedContent')
                                        .putAttribute('aria-expanded', 'false')
                                        .putAttribute('aria-label', 'Toggle navigation')
                                        .build(),
                                new HTMLDiv().builder.addClasses('collapse', 'navbar-collapse').setId('navbarSupportedContent').build()
                        ).build()
                ).build()
        )
        topElement.children.first().children.first().children.last()
    }

    @Override
    IHTMLElement blockAjax(IHTMLElement topElement, String blockId) {
        IHTMLElement e = new HTMLAjaxBlock(blockId).builder.setTaackTag(TaackTag.AJAX_BLOCK).build()
        if (topElement) topElement.addChildren(e)
        topElement ?: e
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
    IHTMLElement blockTabs(IHTMLElement topElement, int tabIds, List<String> names, BlockSpec.Width width) {
        return null
    }

    @Override
    IHTMLElement blockTab(IHTMLElement topElement, int occ) {
        return null
    }
}
