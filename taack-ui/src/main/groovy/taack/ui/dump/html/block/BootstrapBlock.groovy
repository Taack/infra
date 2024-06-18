package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.form.FormSpec
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.element.HTMLButton
import taack.ui.dump.html.element.HTMLDiv
import taack.ui.dump.html.element.HTMLNav
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.layout.BootstrapLayout
import taack.ui.dump.html.theme.ThemeMode
import taack.ui.dump.html.theme.ThemeSize

@CompileStatic
final class BootstrapBlock extends BootstrapLayout implements IHTMLElement {


    BootstrapBlock(BlockLog blockLog) {
        super(blockLog)
    }

    static IHTMLElement block(IHTMLElement topElement, String blockId) {
        topElement.addChildren(
                new HTMLDiv().builder.addClasses('taackBlock', 'container-fluid', 'border').putAttribute('blockId', blockId).build()
        )
        topElement.children.first()
    }

    static IHTMLElement blockHeader(IHTMLElement topElement) {
        HTMLDiv div = new HTMLDiv().builder.addClasses('container-fluid').setId('dropdownNav').addChildren(
                new HTMLButton(null, '<span class="navbar-toggler-icon"></span>').builder
                        .addClasses('navbar-toggler', 'navbar-dark')
                        .putAttribute('data-bs-toggle', 'collapse')
                        .putAttribute('data-bs-target', '#navbarSupportedContent')
                        .putAttribute('aria-controls', 'navbarSupportedContent')
                        .putAttribute('aria-expanded', 'false')
                        .putAttribute('aria-label', 'Toggle navigation')
                        .build(),
                new HTMLDiv().builder.addClasses('collapse', 'navbar-collapse').setId('navbarSupportedContent').build()
        ).build() as HTMLDiv

        topElement.addChildren(
                new HTMLNav().builder.addClasses('navbar', 'navbar-expand-md').addChildren(
                        div
                ).build()
        )
        div
    }

    static IHTMLElement blockAjax(IHTMLElement topElement, String blockId, boolean isAjax) {
        IHTMLElement e = isAjax ? new HTMLAjaxBlock(blockId).builder.build() : new HTMLDiv().builder.putAttribute('ajaxBlockId', blockId).build()
        topElement.addChildren(e)
        e
    }
}
