package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dump.Parameter
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.element.HTMLButton
import taack.ui.dump.html.element.HTMLDiv
import taack.ui.dump.html.element.HTMLNav
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.layout.BootstrapLayout

@CompileStatic
final class BootstrapBlock extends BootstrapLayout implements IHTMLElement {
    static int blockCounter = 0

    BootstrapBlock(BlockLog blockLog, Parameter parameter) {
        super(blockLog, parameter)
        blockCounter++
        if (blockCounter > 1000) blockCounter = 0
    }

    static IHTMLElement block(IHTMLElement topElement, String blockId) {
        topElement.addChildren(
                new HTMLDiv().builder.addClasses('taackBlock', 'container-fluid', 'border').putAttribute('blockId', blockId).addClasses('overflow-y-auto').build()
        )
        topElement.children.first()
    }

    IHTMLElement blockHeader(IHTMLElement topElement) {
        final int bc = blockCounter ++
        HTMLDiv menuDiv = new HTMLDiv().builder.addClasses('collapse', 'navbar-collapse').setId('navbarSupportedContent' + bc).build() as HTMLDiv
        if (parameter.target != Parameter.RenderingTarget.MAIL) {
            HTMLDiv div = new HTMLDiv().builder.addClasses('container-fluid').setId('dropdownNav').addChildren(
                    new HTMLButton(null, '<span class="navbar-toggler-icon"></span>').builder
                            .addClasses('navbar-toggler', 'navbar-dark')
                            .putAttribute('data-bs-toggle', 'collapse')
                            .putAttribute('data-bs-target', '#navbarSupportedContent' + bc)
                            .putAttribute('aria-controls', 'navbarSupportedContent')
                            .putAttribute('aria-expanded', 'false')
                            .putAttribute('aria-label', 'Toggle navigation')
                            .build(),
                    menuDiv
            ).build() as HTMLDiv

            topElement.addChildren(
                    new HTMLNav().builder.addClasses('navbar', 'navbar-expand-md').addChildren(
                            div
                    ).build()
            )
        } else {
            HTMLDiv div = new HTMLDiv().builder.addClasses('container-fluid').setId('dropdownNav').addChildren(
                    menuDiv
            ).build() as HTMLDiv

            topElement.addChildren(
                    new HTMLNav().builder.addClasses('navbar', 'navbar-expand-md').addChildren(
                            div
                    ).build()
            )
        }
        menuDiv
    }

    static IHTMLElement blockAjax(IHTMLElement topElement, String blockId, boolean isAjax, boolean isModalRefresh) {
        IHTMLElement e = isAjax && !isModalRefresh ? new HTMLAjaxBlock(blockId).builder.build() : new HTMLDiv().builder.putAttribute('ajaxBlockId', blockId).build()
        topElement.addChildren(e)
        e
    }
}
