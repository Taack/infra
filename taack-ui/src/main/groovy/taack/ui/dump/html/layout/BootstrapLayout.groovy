package taack.ui.dump.html.layout

import groovy.transform.CompileStatic
import taack.ui.dsl.block.BlockSpec.Width
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.element.*

@CompileStatic
class BootstrapLayout {

    final BlockLog blockLog
    static int tabIdsConter = 0
    final int tabIds


    BootstrapLayout(final BlockLog blockLog) {
        this.blockLog = blockLog
        tabIdsConter++
        tabIds = tabIdsConter
        if (tabIdsConter > 32_000) tabIdsConter = 0
    }

    IHTMLElement tabs(IHTMLElement topElement, List<String> names) {
        IHTMLElement[] elements = new IHTMLElement[names.size()]
        names.eachWithIndex{ String entry, int i ->
            elements[i] = new HTMLLi().builder.addClasses('nav-item').putAttribute('role', 'presentation').addChildren(
                    new HTMLButton(entry).builder.addClasses('nav-link', i==0 ? 'active':null)
                            .putAttribute('data-bs-toggle', 'tab')
                            .putAttribute('role', 'tab')
                            .putAttribute('data-bs-target', "#tab-$tabIds-$i-pane")
                            .putAttribute('aria-selected', i==0 ? 'true' : 'false')
                            .putAttribute('aria-controls', "tab-$tabIds-$i-pane")
                            .setId("tab-$tabIds-$i").build()
            ).build()
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

        if (occ == 0)
            tabContent.addClasses('show', 'active')

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
        topElement.builder.addChildren(new HTMLDiv().builder.addClasses('row', 'g-2').build())
        topElement.children.last()
    }

}
