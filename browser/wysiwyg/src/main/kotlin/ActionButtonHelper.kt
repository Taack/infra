import kotlinx.browser.document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLTextAreaElement

fun getDisplayedButtonList(): List<Pair<ActionButton?, List<ActionButton>>> {
    return listOf(Pair(ActionButton.UNDO, listOf()),
        Pair(ActionButton.REDO, listOf()),
        Pair(null, listOf()),
        Pair(ActionButton.BOLD, listOf()),
        Pair(ActionButton.ITALIC, listOf()),
        Pair(ActionButton.UNDERLINE, listOf()),
        Pair(ActionButton.STRIKE_THROUGH, listOf()),
        Pair(ActionButton.CODE, listOf()),
        Pair(ActionButton.BLOCK, listOf()),
        Pair(null, listOf()),
        Pair(ActionButton.HEADING, listOf(ActionButton.HEADING_1, ActionButton.HEADING_2, ActionButton.HEADING_3, ActionButton.HEADING_4, ActionButton.HEADING_5, ActionButton.HEADING_6)),
        Pair(ActionButton.LIST, listOf()),
        Pair(ActionButton.CITATION, listOf()),
        Pair(null, listOf()),
        Pair(ActionButton.HORIZONTAL_LINE, listOf()),
        Pair(ActionButton.LINK, listOf(ActionButton.LINK_FORM)),
        Pair(ActionButton.IMAGE, listOf(ActionButton.IMAGE_FORM)),
        Pair(ActionButton.TABLE, listOf(ActionButton.TABLE_FORM)))
}

fun createActionButton(b: ActionButton, textarea: HTMLTextAreaElement): HTMLButtonElement {
    val button = document.createElement("button") as HTMLButtonElement
    button.setAttribute("type", "button")
    button.className = b.className
    button.onclick = { b.mouseClickCallBack(textarea, it) }
    button.innerHTML = b.icon(textarea)
    return button
}

fun getLinkFormHtml(textarea: HTMLTextAreaElement): String {
    return """<div>
        |Text: <input type="text" id="${textarea.id}-link-form-text" autocomplete="off">
        |Link: <input type="text" id="${textarea.id}-link-form-link" autocomplete="off">
        |<img src="/assets/check.png" id="${textarea.id}-link-form-confirm" width="20" style="float: right; cursor: pointer">
        |</div>""".trimMargin()
}

fun getImageFormHtml(textarea: HTMLTextAreaElement): String {
    return """<div>
        |Image: <span id="${textarea.id}-image-form-src"></span>
        |Size: <input type="text" id="${textarea.id}-image-form-width" oninput="this.value=this.value.replace(/[^\d]/,'').substring(0, 3)" autocomplete="off" placeholder="Optional">
        |<img src="/assets/check.png" id="${textarea.id}-image-form-confirm" width="20" style="float: right; cursor: pointer">
        |</div>""".trimMargin()
}

fun getTableFormHtml(textarea: HTMLTextAreaElement, rank: Int): String {
    var result = ""
    if (rank > 0) {
        for (i in 1..rank) {
            result += """<div class="${textarea.id}-table-line">"""
            for (j in 1..rank) {
                result += """<span class="wysiwyg-table-unit ${textarea.id}-table-unit" row="$i" column="$j"></span>"""
            }
            result += "</div>"
        }
    }
    result += """<div>
        |<input type="number" id="${textarea.id}-table-column" class="wysiwyg-table-input" min="1" autocomplete="off">
        | X <input type="number" id="${textarea.id}-table-row" class="wysiwyg-table-input" min="1" autocomplete="off">
        |<img src="/assets/check.png" id="${textarea.id}-table-confirm" width="20" style="margin-left: 3px; cursor: pointer">
        |</div>""".trimMargin()
    return result
}