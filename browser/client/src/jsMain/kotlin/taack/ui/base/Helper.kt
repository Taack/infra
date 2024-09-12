package taack.ui.base

import kotlinx.browser.window
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.asList
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.url.URL
import org.w3c.fetch.RequestInit
import org.w3c.files.Blob
import org.w3c.xhr.FormData
import taack.ui.base.element.Block
import taack.ui.base.element.Filter
import taack.ui.base.element.Form
import kotlin.js.Promise

typealias CloseModalPostProcessing = ((String, String, Map<String, String>) -> Unit)

class Helper {
    companion object {
        private var level = 0
        private const val BLOCK_START = "__ajaxBlockStart__"
        private const val BLOCK_END = "__ajaxBlockEnd__"
        private const val OPEN_MODAL = "__openModal__:"
        private const val REFRESH_MODAL = "__refreshModal__:"
        private const val CLOSE_LAST_MODAL = "__closeLastModal__:"
        private const val CLOSE_LAST_MODAL_AND_UPDATE_BLOCK = "__closeLastModalAndUpdateBlock__:"
        private const val FIELD_INFO = ":__FieldInfo__:"
        private const val FIELD_INFO_END = ":__FieldInfoEnd__"
        private const val RELOAD = "__reload__"
        private const val REDIRECT = "__redirect__"
        private const val ERROR_START = "__ErrorKeyStart__"

        fun trace(level: Int, message: String) {
            var s = ""
            for (i in 0..level) {
                s += "    "
            }
            println(s + message)
        }

        fun trace(message: String) {
            trace(level, message)
        }

        fun traceIndent(message: String) {
            trace(level++, message)
        }

        fun traceDeIndent(message: String) {
            trace(--level, message)
        }

        fun mapAjaxErrors(text: String): Map<String, String> {
            val m = mutableMapOf<String, String>()
            val errStart = "__ErrorKeyStart__"
            val errEnd = "__ErrorKeyEnd__"

            if (text.startsWith(errStart)) {

                var pos1 = errStart.length
                var pos2 = text.indexOf(':')
                do {
                    val abId = text.substring(pos1, pos2)
                    pos1 = text.indexOf(errEnd, pos2)
                    val content = text.substring(pos2 + 1, pos1)
                    pos1 += errEnd.length + errStart.length
                    pos2 = text.indexOf(':', pos1)

                    m[abId] = content
                } while (pos2 != -1)
            }
            return m
        }

        fun filterForm(
            filter: Filter,
            offset: Int? = null,
            sort: String? = null,
            order: String? = null,
            b: HTMLButtonElement? = null
        ) {
            b?.disabled = true
            val innerText = b?.innerText
            b?.innerText = "Submitting ..."
            val f = filter.f
            val fd = FormData(f)
            fd.set("isAjax", "true")
            fd.set("refresh", "true")
            fd.set("filterTableId", filter.filterId)
            fd.set("ajaxBlockId", filter.parent.blockId)
            if (offset != null) fd.set("offset", offset.toString())
            if (sort != null) fd.set("sort", sort)
            if (order != null && order != "neutral") fd.set("order", order)
//            else fd.delete("order")

            window.fetch(b?.formAction ?: f.action, RequestInit(method = "POST", body = fd)).then {
                if (it.ok) {
                    it.text()
                } else {
                    trace(it.statusText)
                    Promise.reject(Throwable())
                }
            }.then {
                processAjaxLink(it, filter)
            }.then {
                b?.disabled = false
                if (innerText != null) b?.innerText = innerText
            }
        }

        fun mapAjaxBlock(text: String): Map<String, String> {
            trace("Mapping Ajax Content ... ${text.substring(0, 10)}")
            val m = mutableMapOf<String, String>()
            if (text.startsWith(BLOCK_START)) {
                var pos1 = BLOCK_START.length
                var pos2 = text.indexOf(':')
                do {
                    val abId = text.substring(pos1, pos2)
                    pos1 = text.indexOf(BLOCK_END, pos2)
                    val content = text.substring(pos2 + 1, pos1)
                    pos1 += BLOCK_END.length
                    pos1 += BLOCK_START.length
                    pos2 = text.indexOf(':', pos1)

                    m[abId] = content.substring(pos1, pos2)
                } while (pos2 != -1)
            }
            return m
        }

        val processingStack: ArrayDeque<CloseModalPostProcessing> = ArrayDeque()

        fun processAjaxLink(text: String, base: BaseElement, process: CloseModalPostProcessing? = null) {
            val block = base.getParentBlock()
            when {
                text.contains(RELOAD) -> {
                    window.location.href = (Block.href ?: "")
                }

                text.startsWith(CLOSE_LAST_MODAL) -> {
                    val pos = text.indexOf(':', CLOSE_LAST_MODAL.length)
                    if (text[CLOSE_LAST_MODAL.length] != ':' || text.subSequence(
                            text.length - FIELD_INFO_END.length,
                            text.length
                        ) == FIELD_INFO_END
                    ) {
                        var posField = text.indexOf(FIELD_INFO)
                        if (processingStack.isNotEmpty()) {
                            trace("Helper::process")
                            val id = text.substring(CLOSE_LAST_MODAL.length, pos)
                            val value =
                                if (posField == -1) text.substring(pos + 1) else text.substring(pos + 1, posField)
                            var otherField = emptyMap<String, String>()
                            while (posField != -1) {
                                val endFieldNameIndex = text.indexOf(':', posField + FIELD_INFO.length)
                                val fieldName = text.substring(posField + FIELD_INFO.length, endFieldNameIndex)
                                val endFieldValueIndex = text.indexOf(FIELD_INFO_END, endFieldNameIndex)
                                val fieldValue = text.substring(endFieldNameIndex + 1, endFieldValueIndex)
                                otherField = otherField.plus(Pair(fieldName, fieldValue))
                                posField = text.indexOf(FIELD_INFO, endFieldValueIndex)
                            }
                            val f = processingStack.removeLast()
                            f(id, value, otherField)
                        }
                    } else {
                        if (text.length > CLOSE_LAST_MODAL.length + 1 && text.substring(CLOSE_LAST_MODAL.length + 1)
                                .startsWith(BLOCK_START)
                        ) {
                            mapAjaxBlock(text.substring(CLOSE_LAST_MODAL.length + 1)).map {
                                val target = block.parent?.parent?.ajaxBlockElements?.get(it.key)
                                target!!.d.innerHTML = it.value
                                target.refresh()
                            }
                        }
                    }
                    trace("Helper::closing Modal ${block.modal.mId}")
                    if (block.parent != null) block.parent.close()
                    else block.modal.close()
                }

                text.startsWith(CLOSE_LAST_MODAL_AND_UPDATE_BLOCK) -> {
                    trace("Helper::CLOSE_LAST_MODAL_AND_UPDATE_BLOCK ${block.modal.mId}")
                    if (block.parent != null) block.parent.close()
                    else block.modal.close()
                    val innerText = text.substring(CLOSE_LAST_MODAL_AND_UPDATE_BLOCK.length)
                    processAjaxLink(innerText, base, process)
//                    if (innerText.startsWith(BLOCK_START)) {
//                        mapAjaxBlock(innerText.substring(29)).map {
//                            val target = block.ajaxBlockElements?.get(it.key)
//                                ?: block.parent!!.parent.ajaxBlockElements!![it.key]
//                            target!!.d.innerHTML = it.value
//                            target.refresh()
//                        }
//                    } else if (text[29] == '<') {
//                        if (block.parent != null) block.parent.open(text.substring(29))
//                        else block.modal.open(text.substring(29))
//
//                    } else if (text.substring(29) == RELOAD) {
//                        window.location.href = Block.href ?: ""
//                    }
                }

                text.startsWith(BLOCK_START) -> {
                    mapAjaxBlock(text).map {
                        val target = block.ajaxBlockElements.get(it.key)
                        var pos1 = 0
                        if (it.value.startsWith(BLOCK_START))
                            pos1 += it.value.indexOf(':') + 1
                        var pos2 = it.value.length - pos1
                        if (it.value.endsWith(BLOCK_END))
                            pos2 -= BLOCK_END.length
                        target!!.d.innerHTML = it.value.substring(pos1, pos2)//.substring(it.value.indexOf(':') + 1)
                        target.refresh()
                    }
                }

                text.startsWith(OPEN_MODAL) -> {
                    trace("Helper::opening modal ...")
                    if (process != null) {
                        processingStack.add(process)
                    }
                    block.modal.open(text.substring(OPEN_MODAL.length))
                    val s = block.modal.dModalBody.getElementsByTagName("script").asList()
                    trace("Executing $s")
                }

                text.startsWith(REFRESH_MODAL) -> {
                    trace("Helper::refresh modal $text")
                    if (process != null) {
                        processingStack.add(process)
                    }
                    block.modal.dModalBody.innerHTML = text
                    val s = block.modal.dModalBody.getElementsByTagName("script").asList()
                    trace("Executing $s")
                }

                text.startsWith(REDIRECT) -> {
                    trace("Helper::redirect ${text.substring(REDIRECT.length)}")
                    window.location.href = text.substring(REDIRECT.length)
                }

                text.startsWith(ERROR_START) -> {
                    var hasErrors = false
                    (base as Form).cleanUpErrors()
                    val map = mapAjaxErrors(text).map { me ->
                        hasErrors = true
                        val d = base.errorPlaceHolders[me.key]?.d
                        if (d != null) {
                            d.innerHTML = me.value
                            d.style.display = "block"
                        }
                    }
                    if (!hasErrors) {
                        trace("FormActionButton::hasNoErrors")
                    } else {
                        trace("FormActionButton::hasErrors $map")
                    }
                }

                else -> {
                    if (text.isNotEmpty()) {
                        trace("Helper::update current block")
                        base.getParentBlock().updateContent(text)
                    }
                }

            }
        }

        fun saveOrOpenBlob(blob: Blob, fileName: String) {
            trace("Helper::saveOrOpenBlob blob.size: ${blob.size}, fileName: ${fileName}")
            var a = window.document.createElement("a") as HTMLAnchorElement
            a.href = URL.createObjectURL(blob)
            a.download = fileName
            a.dispatchEvent(MouseEvent("click"))
        }

    }
}
