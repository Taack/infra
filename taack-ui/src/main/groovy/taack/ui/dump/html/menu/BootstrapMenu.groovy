package taack.ui.dump.html.menu

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.*
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
        IHTMLElement bootstrapMenu = this//new BootstrapMenu(themeMode, themeSize)
        children = []
        topElement.addChildren(
                bootstrapMenu.builder.addChildren(
                        new HTMLUl().builder.addClasses('navbar-nav', 'me-auto', 'mb-2', 'mb-lg-0').build()
                ).build()
        )
        return bootstrapMenu.children.first()
    }

    static IHTMLElement splitMenuStart(IHTMLElement topElement) {
        topElement.addChildren(
                new HTMLUl().builder.addClasses('navbar-nav', 'flex-row', 'ml-md-auto').build()
        )
        topElement.children.last()
    }

    static IHTMLElement label(IHTMLElement topElement, String i18n, boolean hasClosure) {
        if (hasClosure) {
            HTMLUl ul = new HTMLUl().builder.addClasses('dropdown-menu').build() as HTMLUl
            topElement.addChildren(
                    new HTMLLi().builder.addClasses('nav-item', 'dropdown').addChildren(
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

    static IHTMLElement menu(IHTMLElement topElement, String i18n, boolean isAjax, String url) {
        topElement.addChildren(
                new HTMLLi().builder.addClasses('nav-item', 'dropdown').addChildren(
                        new HTMLAnchor(false, url).builder.addClasses('nav-link', 'taackMenu').addChildren(new HTMLTxtContent(i18n)).build()
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
                new HTMLUl().builder.addClasses('navbar-nav', 'flex-row', 'ml-md-auto').addChildren(
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
