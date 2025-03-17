package taack.ui.wysiwyg.parser

import taack.ui.wysiwyg.structure.TextOutline

interface IDumper {
    fun dump(outlines: List<TextOutline>): String
}