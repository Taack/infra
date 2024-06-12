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


    IHTMLElement menuStart(IHTMLElement topElement = null) {
        topElement ?= this
        topElement.addChildren(
                new HTMLUl().builder.addClasses('navbar-nav', 'me-auto', 'mb-2', 'mb-lg-0').setTaackTag(TaackTag.MENU).build()
        )
        return topElement.children.last()
    }

    static IHTMLElement splitMenuStart(IHTMLElement topElement) {
        topElement.addChildren(
                new HTMLUl().builder.addClasses('navbar-nav', 'flex-row', 'ml-md-auto').setTaackTag(TaackTag.MENU_SPLIT).build()
        )
        topElement.children.last()
    }

    static IHTMLElement label(IHTMLElement topElement, String i18n, boolean hasClosure) {
        if (hasClosure) {
            HTMLUl ul = new HTMLUl().builder.addClasses('dropdown-menu').build() as HTMLUl
            topElement.addChildren(
                    new HTMLLi().builder.addClasses('nav-item', 'dropdown').setTaackTag(TaackTag.LABEL).addChildren(
                            new HTMLAnchor(false, '#').builder
                                    .addClasses('nav-link', 'dropdown-toggle')
                                    .putAttribute('role', 'button')
                                    .putAttribute('data-bs-toggle', 'dropdown')
                                    .putAttribute('aria-expanded', 'false')
                                    .addChildren(
                                            new HTMLTxtContent(i18n)
                                    ).build(),
                            ul
                    ).build()
            )
            println "i18n $i18n, $hasClosure"
            println topElement.children.first().children.last()
            println topElement.children.last().children.last().output
            return ul
        } else {
            topElement.addChildren(
                    new HTMLLi().builder.addClasses('nav-item', 'dropdown').addChildren(
                            new HTMLAnchor(false, '#').builder.addClasses('nav-link').addChildren(
                                    new HTMLTxtContent(i18n)
                            ).build()
                    ).build()
            )
            return topElement
        }
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
        topElement.builder.addChildren(
                new HTMLLi().builder.addClasses('nav-item', 'dropdown').addChildren(
                        new HTMLSpan().builder.addClasses('navbar-text').addChildren(new HTMLTxtContent('<b>' + i18n + '</b>')).build()
                ).build()
        ).build()
    }

    static IHTMLElement menuIcon(IHTMLElement topElement, String iconHtml, String url, boolean isAjax) {
        topElement.addChildren(
                new HTMLLi().builder.addClasses().addChildren(
                        new HTMLAnchor(isAjax, url).builder.addClasses('nav-link').addChildren(new HTMLTxtContent(iconHtml)).build()
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
                new HTMLLi().builder.addClasses('nav-item', 'dropdown').addChildren(
                        new HTMLAnchor(false, url).builder.addClasses('nav-link').addChildren(
                                new HTMLTxtContent(img),
                                new HTMLTxtContent(value)
                        ).build()
                ).build()
        )
        topElement
    }

    static IHTMLElement menuOptionSection(IHTMLElement topElement, String img, String value) {
        topElement.builder.addChildren(
                new HTMLAnchor(false, '#').builder.addClasses('nav-link').addChildren(
                        new HTMLTxtContent(img),
                        new HTMLTxtContent(value)
                ).build()
        ).build()
    }

    static IHTMLElement menuOptions(IHTMLElement topElement, String img, String value) {
        topElement.builder.addChildren(
                new HTMLUl().builder.addClasses('navbar-nav', 'flex-row', 'ml-md-auto').setTaackTag(TaackTag.MENU_OPTION).addChildren(
                        new HTMLLi().builder.addClasses('nav-item', 'dropdown').addChildren(
                                new HTMLAnchor(false, '#').builder
                                        .addClasses('nav-link', 'dropdown-toggle')
                                        .putAttribute('role', 'button')
                                        .putAttribute('data-bs-toggle', 'dropdown')
                                        .putAttribute('aria-haspopup', 'true')
                                        .putAttribute('aria-expanded', 'false')
                                        .addChildren(
                                                new HTMLTxtContent(img),
                                                new HTMLTxtContent(value)
                                        ).build(),
                                new HTMLUl().builder.addClasses('dropdown-menu').build()
                        ).build()
                ).build(),
        )
        topElement.children.last().children.first().children.last()
    }
}
