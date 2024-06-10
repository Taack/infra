package taack.ui.dsl.form

import taack.ui.dump.html.base.IHTMLElement
import taack.ui.dump.html.base.TaackTag
import taack.ui.dump.html.form.IFormTheme

interface IBaseFormVisitor {
    IFormTheme getFormThemed()

    IHTMLElement getTopElement()

    default IHTMLElement closeTags(TaackTag tag) {
        IHTMLElement top = topElement
        while (top && top.taackTag != tag) {
            top = top.parent
        }
        (top?.taackTag == tag ? top?.parent : top) ?: formThemed
    }

}