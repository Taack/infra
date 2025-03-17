package taack.ui.wysiwyg.parser

import taack.ui.wysiwyg.structure.TextOutline

interface IParser {
    fun parse(html: String, factory: IFactory): List<TextOutline>
}