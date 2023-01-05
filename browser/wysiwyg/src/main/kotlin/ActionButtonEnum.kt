import kotlinx.browser.document
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event

const val NORMAL_BUTTON_CLASS_NAME = "wysiwyg-action-button"
const val SUB_BUTTON_CLASS_NAME = "wysiwyg-dropdown-item"
const val SEPARATOR_CLASS_NAME = "wysiwyg-action-button-separator"

enum class ActionButton(val icon: (textarea: HTMLTextAreaElement) -> String, val className: String, val mouseClickCallBack: (textarea: HTMLTextAreaElement, event: Event) -> Unit) {
    UNDO({ """<img width="20" height="20" src="/assets/icons/undo.svg">""" }, NORMAL_BUTTON_CLASS_NAME, fun (_: HTMLTextAreaElement, _: Event) { document.execCommand("undo") }),
    REDO({ """<img width="20" height="20" src="/assets/icons/redo.svg">""" }, NORMAL_BUTTON_CLASS_NAME, fun (_: HTMLTextAreaElement, _: Event) { document.execCommand("redo") }),
    BOLD({ "<b>B</b>" }, NORMAL_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "**", "**", MarkdownType.SELECTION_START_END) }),
    ITALIC({ "<b><i>I</i></b>" }, NORMAL_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "*", "*", MarkdownType.SELECTION_START_END) }),
    UNDERLINE({ "<b><u>U</u></b>" }, NORMAL_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "<u>", "</u>", MarkdownType.SELECTION_START_END) }),
    STRIKE_THROUGH({ "<b><s>S</s></b>" }, NORMAL_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "<s>", "</s>", MarkdownType.SELECTION_START_END) }),
    CODE({ "<b>&lt;/&gt;</b>" }, NORMAL_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "`", "`", MarkdownType.SELECTION_START_END) }),
    BLOCK({ "<b>[ ]</b>" }, NORMAL_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "```", "```", MarkdownType.LINES_START_END) }),
    HEADING({ "<b>H</b>" }, NORMAL_BUTTON_CLASS_NAME, fun (_: HTMLTextAreaElement, _: Event) {}),
    HEADING_1({ "<h1>Heading 1</h1>" }, SUB_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "# ", null, MarkdownType.EACH_LINE_START) }),
    HEADING_2({ "<h2>Heading 2</h2>" }, SUB_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "## ", null, MarkdownType.EACH_LINE_START) }),
    HEADING_3({ "<h3>Heading 3</h3>" }, SUB_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "### ", null, MarkdownType.EACH_LINE_START) }),
    HEADING_4({ "<h4>Heading 4</h4>" }, SUB_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "#### ", null, MarkdownType.EACH_LINE_START) }),
    HEADING_5({ "<h5>Heading 5</h5>" }, SUB_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "##### ", null, MarkdownType.EACH_LINE_START) }),
    HEADING_6({ "<h6>Heading 6</h6>" }, SUB_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "###### ", null, MarkdownType.EACH_LINE_START) }),
    LIST({ """<img width="20" height="20" src="/assets/icons/list.svg">""" }, NORMAL_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "- ", null, MarkdownType.EACH_LINE_START) }),
    CITATION({ "<b>&#8243; &#8243;</b>" }, NORMAL_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "> ", null, MarkdownType.EACH_LINE_START) }),
    HORIZONTAL_LINE({ "<b>&#8213;</b>" }, NORMAL_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { applyMarkdown(t, "---", null, MarkdownType.ALONE_LINE) }),
    LINK({ """<img width="18" height="18" src="/assets/icons/link.svg">""" }, NORMAL_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { resetLinkForm(t) }),
    LINK_FORM({ getLinkFormHtml(it) }, "$SUB_BUTTON_CLASS_NAME hover-no-background", fun (t: HTMLTextAreaElement, e: Event) { applyLinkMarkdown(t, e) }),
    IMAGE({ """<img width="20" height="20" src="/assets/icons/image.svg">""" }, NORMAL_BUTTON_CLASS_NAME, fun (_: HTMLTextAreaElement, _: Event) {}),
    IMAGE_FORM({ getImageFormHtml(it) }, "$SUB_BUTTON_CLASS_NAME hover-no-background", fun (t: HTMLTextAreaElement, e: Event) { applyImageMarkdown(t, e) }),
    TABLE({ """<img width="20" height="20" src="/assets/icons/table.svg">""" }, NORMAL_BUTTON_CLASS_NAME, fun (t: HTMLTextAreaElement, _: Event) { resetTable(t) }),
    TABLE_FORM({ getTableFormHtml(it, 9) }, "$SUB_BUTTON_CLASS_NAME hover-no-background", fun (t: HTMLTextAreaElement, _: Event) { applyTableMarkdownBySelect(t) })
}