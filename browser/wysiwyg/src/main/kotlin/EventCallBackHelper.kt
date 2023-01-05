import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
import kotlin.js.Promise
import kotlin.js.RegExp

var timer: Int? = null

fun updatePreviewAfterInput(textarea: HTMLTextAreaElement) {
    if (timer != null) {
        window.clearTimeout(timer!!)
    }
    timer = window.setTimeout({ updatePreview(textarea) }, 500)
}

fun updatePreview(textarea: HTMLTextAreaElement) {
    val data = FormData()
    data.append("body", textarea.value)
    window.fetch("/markdown/showPreview", RequestInit(method = "POST", body = data)).then {
        if (it.ok) {
            it.text()
        } else {
            Promise.reject(Throwable())
        }
    }.then { response: String ->
        document.getElementById("${textarea.id}-markdown-preview")!!.innerHTML = response
    }
}

enum class MarkdownType {
    SELECTION_START_END,
    LINES_START_END,
    EACH_LINE_START,
    ALONE_LINE
}

fun applyMarkdown(textarea: HTMLTextAreaElement, prefix: String, suffix: String?, type: MarkdownType) {
    if (textarea != document.activeElement) {
        textarea.focus()
    }
    val text = textarea.value
    var startPos = if (textarea.selectionStart != null) textarea.selectionStart!! else 0
    var endPos = if (textarea.selectionEnd != null) textarea.selectionEnd!! else 0
    var changedValue: String
    when (type) {
        MarkdownType.SELECTION_START_END -> {
            changedValue = prefix + text.substring(startPos, endPos) + suffix
        }
        MarkdownType.LINES_START_END -> {
            changedValue = prefix + "\n" + text.substring(startPos, endPos) + "\n" + suffix
            if (startPos > 0 && text[startPos - 1].toString() != "\n") {
                changedValue = "\n" + changedValue
            }
            if (text[endPos].toString() != "\n") {
                changedValue += "\n"
            }
        }
        MarkdownType.EACH_LINE_START -> {
            changedValue = text.substring(startPos, endPos).split("\n").joinToString(separator = "\n" + prefix)
            val frontText = text.substring(0, startPos).split("\n").last()
            changedValue = prefix + frontText + changedValue
            startPos -= frontText.length
        }
        MarkdownType.ALONE_LINE  -> {
            changedValue = prefix
            if (startPos > 0 && text[startPos - 1].toString() != "\n") {
                changedValue = "\n" + changedValue
            }
            if (text[startPos].toString() != "\n") {
                changedValue += "\n"
            }
            endPos = startPos
        }
    }
    textarea.setSelectionRange(startPos, endPos)
    document.execCommand("insertText", true, changedValue)

    textarea.focus()
    if (type == MarkdownType.SELECTION_START_END || type == MarkdownType.LINES_START_END) {
        if (startPos == endPos) {
            textarea.setSelectionRange(startPos + prefix.length, startPos + prefix.length)
        } else {
            textarea.setSelectionRange(startPos, startPos + changedValue.length)
        }
    }
    textarea.click() // close dropdown menu
}

fun resetLinkForm(textarea: HTMLTextAreaElement) {
    var selectionText = ""
    val startPos = if (textarea.selectionStart != null) textarea.selectionStart!! else 0
    val endPos = if (textarea.selectionEnd != null) textarea.selectionEnd!! else 0
    if (startPos != endPos) {
        selectionText = textarea.value.substring(startPos, endPos)
    }
    (document.getElementById("${textarea.id}-link-form-text") as HTMLInputElement).value = selectionText
    (document.getElementById("${textarea.id}-link-form-link") as HTMLInputElement).value = ""
}

fun applyLinkMarkdown(textarea: HTMLTextAreaElement, event: Event) {
    if (event.target is HTMLImageElement && (event.target as HTMLImageElement).id == "${textarea.id}-link-form-confirm") {
        val startPos = if (textarea.selectionStart != null) textarea.selectionStart!! else 0
        val endPos = if (textarea.selectionEnd != null) textarea.selectionEnd!! else 0

        val textInput = document.getElementById("${textarea.id}-link-form-text") as HTMLInputElement
        val linkInput = document.getElementById("${textarea.id}-link-form-link") as HTMLInputElement
        if (textInput.value.isNotEmpty() && linkInput.value.isNotEmpty()) {
            if (textarea != document.activeElement) {
                textarea.focus()
            }
            val changedValue = "[${textInput.value}](${linkInput.value})"
            textarea.setSelectionRange(startPos, endPos)
            document.execCommand("insertText", true, changedValue)

            textarea.focus()
            textarea.setSelectionRange(startPos, startPos + changedValue.length)
            textarea.click()
        }
    }
}

fun resetImageForm(textarea: HTMLTextAreaElement) {
    (document.getElementById(textarea.id + "-attachment-select") as HTMLInputElement).value = ""
    (document.getElementById(textarea.id + "-attachment-link") as HTMLInputElement).value = ""
    (document.getElementById("${textarea.id}-image-form-width") as HTMLInputElement).value = ""
}

fun applyImageMarkdown(textarea: HTMLTextAreaElement, event: Event) {
    if (event.target is HTMLImageElement && (event.target as HTMLImageElement).id == "${textarea.id}-image-form-confirm") {
        val src = (document.getElementById(textarea.id + "-attachment-link") as HTMLInputElement).value
        if (src.isNotEmpty()) {
            val alt = (document.getElementById("${textarea.id}-attachment-select") as HTMLInputElement).value
            val width = (document.getElementById("${textarea.id}-image-form-width") as HTMLInputElement).value
            applyMarkdown(textarea, "<img src='$src' alt='$alt' width='$width'>", "", MarkdownType.SELECTION_START_END)
            resetImageForm(textarea)
        }
    }
}

fun resetTable(textarea: HTMLTextAreaElement) {
    (document.getElementById("${textarea.id}-table-column") as HTMLInputElement).value = "1"
    (document.getElementById("${textarea.id}-table-row") as HTMLInputElement).value = "1"
    updateTableByRowAndColumnInput(textarea)
}

fun updateTableByRowAndColumnInput(textarea: HTMLTextAreaElement) {
    val units = document.getElementsByClassName("${textarea.id}-table-unit")
    for (i in 0 until units.length) {
        (units[i] as HTMLSpanElement).style.background = "transparent"
    }

    val column = (document.getElementById("${textarea.id}-table-column") as HTMLInputElement).value.toInt()
    val row = (document.getElementById("${textarea.id}-table-row") as HTMLInputElement).value.toInt()
    val tableLineList = document.getElementsByClassName("${textarea.id}-table-line")
    for (i in 0 until row) {
        if (i < tableLineList.length) {
            val tableLine = tableLineList[i] as HTMLDivElement
            val tableUnitList = tableLine.getElementsByClassName("${textarea.id}-table-unit")
            for (j in 0 until column) {
                if (j < tableUnitList.length) {
                    (tableUnitList[j] as HTMLSpanElement).style.background = if (i == 0) "#0000005c" else "#33333336"
                }
            }
        }
    }
}

fun applyTableMarkdownBySelect(textarea: HTMLTextAreaElement) {
    val column = (document.getElementById("${textarea.id}-table-column") as HTMLInputElement).value.toInt()
    val row = (document.getElementById("${textarea.id}-table-row") as HTMLInputElement).value.toInt()
    if (column > 0 && row > 0) {
        var res = ""
        for (i in 1..row) {
            res += "\n" + "| ${if (i == 1) "Header" else "data"} ".repeat(column) + "|"
            if (i == 1) {
                res += "\n" + "|:------:".repeat(column) + "|"
            }
        }
        applyMarkdown(textarea, res, null, MarkdownType.ALONE_LINE)
    }
}

fun applyTableMarkdownByClipboard(textarea: HTMLTextAreaElement, data: String) {
    if (data.isNotEmpty() && (data.contains("\n") || data.contains("\t"))) {
        var res = ""
        val rows = data.split("\n")
        for (i in 0 until rows.size - 1) {
            val row = rows[i]
            val columns = row.split("\t")
            res += "\n" + "| " + columns.joinToString(separator = " | ") + " |"
            if (i == 0) {
                res += "\n" + "|:------:".repeat(columns.size) + "|"
            }
        }
        applyMarkdown(textarea, res, null, MarkdownType.ALONE_LINE)
    }
}

fun applyListForNextLine(textarea: HTMLTextAreaElement) {
    val text = textarea.value
    val startPos = if (textarea.selectionStart != null) textarea.selectionStart!! else 0
    val lastIndex = text.substring(0, startPos).lastIndexOf("\n")
    val cursorLineStr = text.substring(if (lastIndex == -1) 0 else lastIndex + 1, startPos)
    val pattern = RegExp("^- (?!-)")
    if (pattern.test(cursorLineStr.trimStart())) {
        var blankNumber = 0
        if ((cursorLineStr.isNotEmpty()) && (cursorLineStr[0].isWhitespace())) {
            for (i in cursorLineStr.indices) {
                if (cursorLineStr[i].isWhitespace()) {
                    blankNumber += 1
                } else break
            }
        }
        document.execCommand("insertText", true, "\n" + " ".repeat(blankNumber) + "- ")
    } else {
        document.execCommand("insertText", true, "\n")
    }
    textarea.focus()
}