package taack.ui.wysiwyg.parser

import taack.ui.wysiwyg.structure.InlineFace
import taack.ui.wysiwyg.structure.TextOutline

interface IFactory {
    fun createOutline(textOutline: TextOutline, text: String, inlineFaces: List<InlineFace>) : TextOutline
    fun createInline(inlineFace: InlineFace) : InlineFace
}