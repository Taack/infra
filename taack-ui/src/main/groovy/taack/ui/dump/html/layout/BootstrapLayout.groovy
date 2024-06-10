package taack.ui.dump.html.layout

import groovy.transform.CompileStatic
import taack.ui.dsl.form.FormSpec
import taack.ui.dump.html.element.*

@CompileStatic
class BootstrapLayout {

    static IHTMLElement tabs(IHTMLElement topElement, int tabIds, List<String> names, FormSpec.Width width) {
        HTMLInput[] radioList = new HTMLInput[names.size()]
        HTMLLi[] liList = new HTMLLi[names.size()]
        names.eachWithIndex { it, occ ->
            int tabOcc = occ + 1
            radioList[occ] = HTMLInput.inputRadio(null, "pct-${tabIds}", occ == 0).builder.addClasses("inputTab${tabOcc}").setId("tab$tabOcc-f${tabIds}").build() as HTMLInput
            liList[occ] = new HTMLLi().builder.addClasses("tab${tabOcc}").addChildren(
                    new HTMLLabel("tab${tabOcc}-f${tabIds}").builder.addChildren(
                            new HTMLTxtContent(it)
                    ).build()
            ).build() as HTMLLi
        }

        topElement.builder.addChildren(
                new HTMLDiv().builder
                        .setTaackTag(TaackTag.TABS)
                        .addClasses('pc-tab', width.sectionCss)
                        .addChildren(radioList)
                        .addChildren(
                                new HTMLNav().builder.addChildren(
                                        new HTMLUl().builder.addChildren(liList).build()
                                ).build())
                        .addChildren(new HTMLSection())
                        .build()
        )
        topElement.children.last().children.last()
    }

    static IHTMLElement tab(IHTMLElement topElement, int occ) {
        topElement.builder.addChildren(
                new HTMLDiv().builder.setTaackTag(TaackTag.TAB).addClasses('tab' + occ).build()
        )
        topElement.children.last()
    }

    static IHTMLElement col(IHTMLElement topElement) {
        topElement.builder.addChildren(new HTMLDiv().builder.setTaackTag(TaackTag.COL).addClasses('flex-fill').build())
        topElement.children.last()
    }

    static IHTMLElement row(IHTMLElement topElement) {
        topElement.builder.addChildren(new HTMLDiv().builder.setTaackTag(TaackTag.ROW).addClasses('d-flex', 'flex-row').build())
        topElement.children.last()
    }

}
