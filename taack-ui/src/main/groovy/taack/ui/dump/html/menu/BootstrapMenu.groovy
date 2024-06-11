package taack.ui.dump.html.menu

import groovy.transform.CompileStatic
import taack.ui.IEnumOption
import taack.ui.IEnumOptions
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.form.FormSpec
import taack.ui.dump.html.block.IBlockTheme
import taack.ui.dump.html.element.HTMLAnchor
import taack.ui.dump.html.element.HTMLButton
import taack.ui.dump.html.element.HTMLDiv
import taack.ui.dump.html.element.HTMLForm
import taack.ui.dump.html.element.HTMLInput
import taack.ui.dump.html.element.HTMLLi
import taack.ui.dump.html.element.HTMLNav
import taack.ui.dump.html.element.HTMLSpan
import taack.ui.dump.html.element.HTMLTxtContent
import taack.ui.dump.html.element.HTMLUl
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.element.InputType
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
    }

    static IHTMLElement menuStart(IHTMLElement topElement) {
        topElement.addChildren(
                new HTMLUl().builder.addClasses('navbar-nav', 'me-auto', 'mb-2', 'mb-lg-0').setTaackTag(TaackTag.MENU).build()
        )
        topElement.children.first()
    }

    static IHTMLElement splitMenuStart(IHTMLElement topElement) {
        topElement.addChildren(
                new HTMLUl().builder.addClasses('navbar-nav', 'flex-row', 'ml-md-auto').setTaackTag(TaackTag.MENU).build()
        )
        topElement.children.first()
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

    static IHTMLElement menu(IHTMLElement topElement, String i18n, String url) {
        topElement.addChildren(
                new HTMLLi().builder.addClasses('nav-item', 'dropdown').addChildren(
                        new HTMLAnchor(false, url).builder.addClasses('nav-link').addChildren(new HTMLTxtContent(i18n)).build()
                ).build()
        )
        topElement
    }

    static IHTMLElement section(IHTMLElement topElement, String i18n) {
        topElement.addChildren(
                new HTMLLi().builder.addClasses('nav-item', 'dropdown').addChildren(
                        new HTMLSpan().builder.addClasses('navbar-text').addChildren(new HTMLTxtContent('<b>' + i18n + '</b>')).build()
                ).build()
        )
        topElement
    }

    static IHTMLElement menuIcon(IHTMLElement topElement, String iconHtml, String url, boolean isAjax) {
        topElement.addChildren(
                new HTMLLi().builder.addClasses().addChildren(
                        new HTMLAnchor(isAjax, url).builder.addClasses('navbar-link').addChildren(new HTMLTxtContent(iconHtml)).build()
                ).build()
        )
        topElement
    }

    static IHTMLElement menuSearch(IHTMLElement topElement, String query, String action) {
        topElement.addChildren(
                new HTMLForm(action).builder.addClasses('solrSearch-input', 'py-1').addChildren(
                        new HTMLDiv().builder.addClasses('input-group', 'rounded').addChildren(
                                new HTMLInput(InputType.STRING, query, 'q', 'Search').builder.putAttribute('aria-label', 'Search').addClasses('form-control', 'rounded', 'bg-white').build()
                        ).build()
                ).build()
        )
        topElement
    }

    static IHTMLElement menuOption(IHTMLElement topElement, String img, String value, String url) {
        topElement.addChildren(
                new HTMLAnchor(false, url).builder.addClasses('nav-link').addChildren(
                        new HTMLTxtContent(img),
                        new HTMLTxtContent(value)
                ).build()
        )
        topElement
    }

    static IHTMLElement menuOptionSection(IHTMLElement topElement, String img, String value) {
        topElement.addChildren(
                new HTMLAnchor(false, '#').builder.addClasses('nav-link').addChildren(
                        new HTMLTxtContent(img),
                        new HTMLTxtContent(value)
                ).build()
        )
        topElement
    }

    static IHTMLElement menuOptions(IHTMLElement topElement, String img, String value) {
        topElement.addChildren(
                new HTMLLi().builder.addClasses('nav-item', 'dropdown').setTaackTag(TaackTag.MENU_OPTION).addChildren(
                        new HTMLAnchor(false, '#').builder.addClasses('nav-link').addChildren(
                                new HTMLTxtContent(img),
                                new HTMLTxtContent(value)
                        ).build()
                ).build()
        )
        topElement.children.first()
    }
}
