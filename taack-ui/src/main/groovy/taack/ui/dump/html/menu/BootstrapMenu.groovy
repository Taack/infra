package taack.ui.dump.html.menu

import groovy.transform.CompileStatic
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.form.FormSpec
import taack.ui.dump.html.block.IBlockTheme
import taack.ui.dump.html.element.HTMLAnchor
import taack.ui.dump.html.element.HTMLDiv
import taack.ui.dump.html.element.HTMLLi
import taack.ui.dump.html.element.HTMLTxtContent
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.layout.BootstrapLayout
import taack.ui.dump.html.theme.ThemeMode
import taack.ui.dump.html.theme.ThemeSize

@CompileStatic
final class BootstrapMenu implements IHTMLElement {

    final ThemeMode themeMode
    final ThemeSize themeSize

    BootstrapMenu(ThemeMode themeMode, ThemeSize themeSize) {
        this.themeMode = themeMode
        this.themeSize = themeSize
        tag = 'ul'
        addClasses('navbar-nav', 'me-auto', 'mb-2', 'mb-lg-0')
        setTaackTag(TaackTag.MENU)
    }

    static IHTMLElement label(IHTMLElement topElement, String i18n, boolean hasClosure) {
        if (hasClosure)
            topElement.builder.addChildren(
                    new HTMLLi().builder.addClasses('nav-item', 'dropdown').setTaackTag(TaackTag.LABEL).addChildren(
                            new HTMLAnchor(false, '#').builder
                                    .addClasses('nav-item', 'dropdown-toggle')
                                    .putAttribute('role', 'button')
                                    .putAttribute('data-bs-toggle', 'dropdown')
                                    .putAttribute('aria-expanded', 'false')
                                    .addChildren(
                                            new HTMLTxtContent(i18n)
                                    ).build()
                    ).build()
            ).build().children.first()
        else
            topElement.builder.addChildren(
                    new HTMLLi().builder.addClasses('nav-item', 'dropdown').addChildren(
                            new HTMLAnchor(false, '#').builder.addClasses('nav-item').addChildren(
                                    new HTMLTxtContent(i18n)
                            ).build()
                    ).build()
            ).build()
    }

}
