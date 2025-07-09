package taack.ui.wysiwyg.contentEditableMono

import taack.ui.base.element.Form
import web.html.HTMLDivElement
import web.html.HTMLTextAreaElement

class MainContentEditable(
    internal val embeddingForm: Form,
    internal val textarea: HTMLTextAreaElement,
    private val divHolder: HTMLDivElement,
    private val divScroll: HTMLDivElement
) {

    class CmdLine {

        data class Span(val pattern: String, val className: String, val inlined: Boolean)

        enum class SpanStyle(val span: Span) {
            TITLE1(Span("#", "mmm", false))
        }


        var content: List<SpanStyle> = emptyList()

        fun draw() {

        }
    }

    init {

    }

    fun spanBold(cmdLine: CmdLine) {

    }

    fun spanCitation(cmdLine: CmdLine) {

    }

    fun spanTitle(cmdLine: CmdLine) {

    }

    fun createCmdLine() {

    }

}