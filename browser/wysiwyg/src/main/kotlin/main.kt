import kotlinx.browser.document
import org.w3c.dom.*
import org.w3c.dom.clipboard.ClipboardEvent
import org.w3c.dom.events.EventListener

fun main() {
    initWysiwyg()
    addModalObserver()
}

fun addModalObserver() { // Init wysiwyg every time a modal is open
    val modalElements = document.getElementsByClassName("modal")
    if (modalElements.length > 0) {
        val modal = modalElements[modalElements.length - 1] as HTMLDivElement
        val observer = MutationObserver(fun (callback: Array<MutationRecord>, _: Any) {
            callback.forEach {
                if (it.type == "attributes" && it.attributeName == "style" && it.target is HTMLDivElement && (it.target as HTMLDivElement).style.display == "block") {
                    initWysiwyg()
                    addModalObserver() // Add the observer for the new modal element
                }
            }
        })
        observer.observe(modal, MutationObserverInit(attributes = true))
    }
}

fun initWysiwyg() {
    val textareaList = document.getElementsByClassName("wysiwyg-content")
    for (i in 0 until textareaList.length) {
        val textarea = textareaList[i]!! as HTMLTextAreaElement
        val parentDiv = document.getElementById(textarea.id + "-editor")!!
        if (!parentDiv.firstElementChild!!.classList.contains("wysiwyg-action-menu")) {
            val actionMenuDiv = document.createElement("div")
            actionMenuDiv.className = "wysiwyg-action-menu"
            parentDiv.prepend(actionMenuDiv)

            getDisplayedButtonList().forEach { entry ->
                val b = entry.first
                if (b != null) {
                    val button = createActionButton(b, textarea)
                    button.title = b.toString().replace("_", " ").lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    button.onmousedown = { it.preventDefault() }
                    actionMenuDiv.append(button)

                    if (entry.second.isNotEmpty()) {
                        button.classList.add("dropdown-toggle")
                        button.setAttribute("data-bs-toggle", "dropdown")
                        button.setAttribute("aria-expanded", "false")
                        button.setAttribute("data-bs-auto-close", "outside")

                        val dropdownDiv = document.createElement("div") as HTMLDivElement
                        dropdownDiv.className = "dropdown-menu"
                        dropdownDiv.style.zIndex = "0"
                        entry.second.forEach {
                            dropdownDiv.append(createActionButton(it, textarea))
                        }
                        actionMenuDiv.append(dropdownDiv)
                    }
                } else {
                    val separator = document.createElement("span")
                    separator.className = SEPARATOR_CLASS_NAME
                    actionMenuDiv.append(separator)
                }
            }

            // init ActionButton.LINK_FORM
            val textInput = document.getElementById("${textarea.id}-link-form-text") as HTMLInputElement
            val linkInput = document.getElementById("${textarea.id}-link-form-link") as HTMLInputElement
            listOf(textInput, linkInput).forEach { input ->
                input.onkeypress = {
                    val key = if (it.keyCode == 0) it.which else it.keyCode
                    if (key == 13) { // Enter
                        it.preventDefault()
                        (document.getElementById("${textarea.id}-link-form-confirm") as HTMLImageElement).click()
                    }
                }
            }

            // init ActionButton.IMAGE_FORM
            val srcSpan = document.getElementById("${textarea.id}-image-form-src") as HTMLSpanElement
            srcSpan.append(document.getElementById(textarea.id + "-attachment-select"))
            srcSpan.append(document.getElementById(textarea.id + "-attachment-link"))
            val widthInput = document.getElementById("${textarea.id}-image-form-width") as HTMLInputElement
            listOf(srcSpan, widthInput).forEach { input ->
                input.onkeypress = {
                    val key = if (it.keyCode == 0) it.which else it.keyCode
                    if (key == 13) { // Enter
                        it.preventDefault()
                        (document.getElementById("${textarea.id}-image-form-confirm") as HTMLImageElement).click()
                    }
                }
            }
            val observer = MutationObserver(fun (callback: Array<MutationRecord>, _: Any) { // reopen image form after select of attachment
                callback.forEach {
                    if (it.type == "attributes" && it.attributeName == "value" && it.target is HTMLInputElement && (it.target as HTMLInputElement).value.isNotEmpty()) {
                        (srcSpan.parentElement!!.parentElement!!.parentElement!!.previousElementSibling as HTMLButtonElement).click()
                    }
                }
            })
            observer.observe(document.getElementById(textarea.id + "-attachment-link") as HTMLInputElement, MutationObserverInit(attributes = true))

            // init ActionButton.TABLE_DETAIL
            val tableUnitList = document.getElementsByClassName("${textarea.id}-table-unit")
            val columnInput = document.getElementById("${textarea.id}-table-column") as HTMLInputElement
            val rowInput = document.getElementById("${textarea.id}-table-row") as HTMLInputElement
            for (k in 0 until tableUnitList.length) {
                val unit = tableUnitList[k] as HTMLSpanElement
                unit.onmouseenter = {
                    columnInput.value = unit.getAttribute("column")!!
                    rowInput.value = unit.getAttribute("row")!!
                    updateTableByRowAndColumnInput(textarea)
                }
            }
            listOf(columnInput, rowInput).forEach { input ->
                input.oninput = {
                    updateTableByRowAndColumnInput(textarea)
                }
                input.onclick = {
                    it.stopPropagation()
                }
                input.onkeypress = {
                    val key = if (it.keyCode == 0) it.which else it.keyCode
                    if (key == 13) { // Enter
                        it.preventDefault()
                        (document.getElementById("${textarea.id}-table-confirm") as HTMLImageElement).click()
                    }
                }
            }

            // can paste a table copied from LibreOffice
            textarea.onpaste = { it: ClipboardEvent ->
                if (it.clipboardData != null) {
                    if (it.clipboardData!!.types.size == 4) { // Todo: find a better way to distinguish type of item
                        it.preventDefault()
                        applyTableMarkdownByClipboard(textarea, it.clipboardData!!.getData("Text"))
                    }
                }
            }

            // can drop image to upload it (in kotlin we can't set file input programmatically, so we use pure javascript here)
            val dropEvent = """
                let files = event.dataTransfer.files; 
                if (files.length > 0) {
                    event.preventDefault(); 
                    let modalElements = document.getElementsByClassName("modal");
                    let mElement = modalElements[modalElements.length - 1];
                    let inputElement = document.getElementById(this.id + '-attachment-select');
                    inputElement.setAttribute("taackAjaxFormM2OParams", "directUpload=true");
                    
                    let observer = new MutationObserver(function(mutationsList, observer) {
                        for (let m of mutationsList) {
                            if (m.type === "attributes" && m.attributeName === "style" && m.target.style.display === "block") {
                                document.getElementById('filePath').files = files;
                                inputElement.setAttribute("taackAjaxFormM2OParams", "directUpload=false");
                                observer.disconnect();
                                break;
                            }
                        }
                    });
                    observer.observe(mElement, { attributes: true });
                    inputElement.click();
                }""".trimIndent()
            textarea.setAttribute("ondrop", dropEvent)
            textarea.ondragover = { // do not show textarea cursor when dragging
                it.preventDefault()
            }
            textarea.ondragenter = {
                textarea.classList.add("wysiwyg-file-dragging")
            }
            textarea.ondragleave = {
                textarea.classList.remove("wysiwyg-file-dragging")
            }
            textarea.addEventListener("drop", EventListener {
                textarea.classList.remove("wysiwyg-file-dragging")
            })

            // can auto check and apply list for next line when press enter
            textarea.onkeypress = {
                val key = if (it.keyCode == 0) it.which else it.keyCode
                if (key == 13) { // Enter
                    it.preventDefault()
                    applyListForNextLine(textarea)
                }
            }

            // can auto update preview after finish of input
            textarea.oninput = {
                updatePreviewAfterInput(textarea)
            }

            // init preview
            if (textarea.value.isNotEmpty()) {
                updatePreview(textarea)
            }
        }
    }
}