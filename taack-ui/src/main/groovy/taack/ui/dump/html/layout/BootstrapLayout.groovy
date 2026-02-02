package taack.ui.dump.html.layout

import groovy.transform.CompileStatic
import taack.ui.dsl.block.BlockSpec.Width
import taack.ui.dump.Parameter
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.element.*

@CompileStatic
class BootstrapLayout {

    final BlockLog blockLog
    static int tabIdsConter = 0
    int tabIds
    final Parameter parameter

    BootstrapLayout(final BlockLog blockLog, Parameter parameter = null) {
        this.blockLog = blockLog
        this.parameter = parameter ?: new Parameter()

        tabIdsConter++
        tabIds = this.parameter.tabId == null ? tabIdsConter : this.parameter.tabId
        if (tabIdsConter > 32_000) tabIdsConter = 0
    }

    IHTMLElement tabs(IHTMLElement topElement, List<String> names, String action = null) {
        Integer tabIndex = this.parameter.tabIndex ?: 0
        IHTMLElement[] elements = new IHTMLElement[names.size()]
        if (parameter.target != Parameter.RenderingTarget.MAIL)
            names.eachWithIndex { String entry, int i ->
                elements[i] = new HTMLLi().builder.addClasses('nav-item').putAttribute('role', 'presentation').addChildren(
                        new HTMLButton(entry).builder.addClasses('nav-link', i == tabIndex ? 'active' : '')
                                .putAttribute('type', 'button')
                                .putAttribute('data-bs-toggle', 'tab')
                                .putAttribute('role', 'tab')
                                .putAttribute('data-bs-target', "#tab-$tabIds-$i-pane")
                                .putAttribute('aria-selected', i == tabIndex ? 'true' : 'false')
                                .putAttribute('aria-controls', "tab-$tabIds-$i-pane")
                                .putAttribute('action', action)
                                .setId("tab-$tabIds-$i").build()
                ).build()
            }
        else
            names.eachWithIndex { String entry, int i ->
                elements[i] = new HTMLDiv().builder.addChildren(new HTMLTxtContent(entry)).build()
            }

        IHTMLElement tabsContent = new HTMLDiv().builder.addClasses('tab-content').setId("tab-content-$tabIds").build()

        topElement.addChildren(
                new HTMLUl().builder.addClasses('nav', 'nav-tabs').setId("myTab$tabIds").putAttribute('role', 'tablist').addChildren(
                        elements
                ).build(),
                tabsContent
        )
        tabsContent
    }


    IHTMLElement tab(IHTMLElement topElement, int occ) {
        IHTMLElement tabContent = new HTMLDiv().builder.addClasses('tab-pane', 'fade')
                .putAttribute('role', 'tabpanel')
                .putAttribute('tabindex', "$occ")
                .putAttribute('aria-labelledby', "tab-$tabIds-$occ")
                .setId("tab-$tabIds-$occ-pane").build()

        Integer tabIndex = this.parameter?.tabIndex ?: 0
        if (occ == tabIndex)
            tabContent.addClasses('show', 'active', 'loaded')

        topElement.builder.addChildren(
                tabContent
        )
        tabContent
    }

    static IHTMLElement col(IHTMLElement topElement, Width width = null) {
        String widthClass = width?.bootstrapCss ?: 'flex-fill'
        topElement.builder.addChildren(new HTMLDiv().builder.addClasses(widthClass).build())
        topElement.children.last()
    }

    static IHTMLElement row(IHTMLElement topElement) {
        topElement.builder.addChildren(new HTMLDiv().builder.addClasses('row', 'gx-2').build())
        topElement.children.last()
    }

    static IHTMLElement rowCols(IHTMLElement topElement, int cols) {
        String c = cols == 0 ? 'auto' : cols.toString()
        topElement.builder.addChildren(new HTMLDiv().builder.addClasses('row', 'row-cols-' + c, 'gx-2').build())
        topElement.children.last()
    }

    void setTabIdAndCounter(int i) {
        tabIds = i
        tabIdsConter = tabIds
    }

    static int accordionCounter = 0

    IHTMLElement accordion(IHTMLElement topElement) {
        accordionCounter++
        if (accordionCounter > 32_000) accordionCounter = 0
        IHTMLElement acc = new HTMLDiv().builder
                .addClasses('accordion')
                .setId("accordion-${accordionCounter}")
                .build()
        topElement.addChildren(acc)
        acc
    }

    IHTMLElement accordionItem(IHTMLElement accordionElement, String title, int itemIndex, boolean openByDefault) {
        String accId = accordionElement.id
        String collapseId = "collapse-${accId}-${itemIndex}"

        IHTMLElement button = new HTMLButton(title).builder
                .addClasses('accordion-button', openByDefault ? '' : 'collapsed')
                .putAttribute('type', 'button')
                .putAttribute('data-bs-toggle', 'collapse')
                .putAttribute('data-bs-target', "#${collapseId}")
                .putAttribute('aria-expanded', openByDefault ? 'true' : 'false')
                .putAttribute('aria-controls', collapseId)
                .build()

        IHTMLElement header = new HTMLH2().builder
                .addClasses('accordion-header')
                .addChildren(button)
                .build()

        IHTMLElement body = new HTMLDiv().builder
                .addClasses('accordion-body')
                .build()

        IHTMLElement collapseDiv = new HTMLDiv().builder
                .setId(collapseId)
                .addClasses('accordion-collapse', 'collapse', openByDefault ? 'show' : '')
                .putAttribute('data-bs-parent', "#${accId}")
                .addChildren(body)
                .build()

        IHTMLElement item = new HTMLDiv().builder
                .addClasses('accordion-item')
                .addChildren(header, collapseDiv)
                .build()

        accordionElement.addChildren(item)
        body
    }

    static IHTMLElement cardStart(IHTMLElement topElement, String title, boolean hasMenu) {
        IHTMLElement card = new HTMLDiv().builder.addClasses('card', 'mb-3').build()
        topElement.addChildren(card)

        if (title || hasMenu) {
            IHTMLElement cardHeader = new HTMLDiv().builder.addClasses('card-header').build()
            if (title) {
                cardHeader.addChildren(new HTMLTxtContent(title))
            }
            card.addChildren(cardHeader)
            return cardHeader
        }
        card
    }

    static IHTMLElement cardBody(IHTMLElement cardDiv) {
        IHTMLElement body = new HTMLDiv().builder.addClasses('card-body').build()
        cardDiv.addChildren(body)
        body
    }

    static IHTMLElement scrollPanel(IHTMLElement topElement, String maxHeight) {
        IHTMLElement panel = new HTMLDiv().builder
                .addClasses('overflow-auto')
                .putAttribute('style', "max-height: ${maxHeight}")
                .build()
        topElement.addChildren(panel)
        panel
    }
}
