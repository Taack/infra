package taack.ui.base

import js.iterable.asSequence
import taack.ui.base.element.Block
import taack.ui.base.element.Filter
import taack.ui.base.element.Form
import web.blob.Blob
import web.dom.document
import web.events.EventHandler
import web.events.EventType
import web.form.FormData
import web.form.FormDataEntryValue
import web.history.history
import web.html.HTMLAnchorElement
import web.html.HTMLButtonElement
import web.http.RequestMethod
import web.location.location
import web.storage.localStorage
import web.uievents.MouseEvent
import web.url.URL
import web.window.window
import web.xhr.XMLHttpRequest

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
        private const val REDIRECT = "__redirect__"
        private const val ERROR_START = "__ErrorKeyStart__"
        const val RELOAD = "__reload__"
        var historyState = HashMap<String, FormDataEntryValue>()
        var traceEnabled = true
        fun trace(level: Int, message: String) {
            if (traceEnabled) {
                var s = ""
                for (i in 0..level) {
                    s += "    "
                }
                println(s + message)
            }
        }

        fun trace(message: String) {
            if (traceEnabled) trace(level, message)
        }

        fun traceIndent(message: String) {
            if (traceEnabled) trace(level++, message)
        }

        fun traceDeIndent(message: String) {
            if (traceEnabled) trace(--level, message)
        }

        private fun mapAjaxErrors(text: String): Map<String, String> {
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
            val formUrl = URL(f.action)
            fd["isAjax"] = "true"
            fd["refresh"] = "true"
            fd["filterTableId"] = filter.filterId
            fd["ajaxBlockId"] = filter.parent.blockId
            if (offset != null) fd["offset"] = offset.toString()
            else {
                if (sort != null) fd["sort"] = sort
                if (order != null && order != "neutral") fd["order"] = order
                else fd.delete("order")
            }

            val xhr = XMLHttpRequest()
            xhr.onloadend = EventHandler {
                //Filter used saved to historyState
                historyState = fd.entries().asSequence().map {
                    it.component1() to it.component2()
                }.toMap() as HashMap<String, FormDataEntryValue>
                hydrateStateToUrl(formUrl)
                history.pushState("{}", document.title, formUrl)
                processAjaxLink(null, xhr.responseText, filter)
                b?.disabled = false
                if (innerText != null) b.innerText = innerText

            }
            xhr.open(RequestMethod.POST, f.action)
            xhr.send(fd)
        }

        fun mapAjaxBlock(text: String): Map<String, String> {
            trace("Mapping Ajax Content ... ${text.substring(0, 10)}")
            val m = mutableMapOf<String, String>()
            if (text.startsWith(BLOCK_START)) {
                var pos1 = 0
                do {
                    pos1 += BLOCK_START.length
                    val pos2 = text.indexOf(':', pos1)
                    val abId = text.substring(pos1, pos2)
                    pos1 = text.indexOf(BLOCK_END, pos2)
                    m[abId] = text.substring(pos2 + 1, pos1)

                    pos1 = text.indexOf(BLOCK_START, pos1)
                } while (pos1 != -1)
            }
            return m
        }

        private val processingStack: ArrayDeque<CloseModalPostProcessing> = ArrayDeque()
        val urlStack: ArrayDeque<URL> = ArrayDeque()

        fun processAjaxLink(url: URL? = null, text: String, base: BaseElement?, process: CloseModalPostProcessing? = null) {
            val block = base?.getParentBlock() ?: Block.getSiblingBlock(null)!!
            when {
                text.startsWith(RELOAD) -> {
                    localStorage.setItem("y-scroll", window.scrollY.toString())
                    location.href = (Block.href ?: "")
                }

                text.startsWith(CLOSE_LAST_MODAL) -> {
                    if (url != null) urlStack.removeLast()
                    val pos = text.indexOf(':', CLOSE_LAST_MODAL.length)
                    if (text[CLOSE_LAST_MODAL.length] != ':' || text.subSequence(
                            text.length - FIELD_INFO_END.length,
                            text.length
                        ) == FIELD_INFO_END
                    ) {
                        var posField = text.indexOf(FIELD_INFO)
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
                        if (processingStack.isNotEmpty()) {
                            trace("Helper::process")
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
                    if (url != null) {
                        urlStack.removeLast()
                        urlStack.addLast(url)
                    }
                    trace("Helper::CLOSE_LAST_MODAL_AND_UPDATE_BLOCK ${block.modal.mId}")
                    if (block.parent != null) block.parent.close()
                    else block.modal.close()
                    val innerText = text.substring(CLOSE_LAST_MODAL_AND_UPDATE_BLOCK.length)
                    processAjaxLink(url, innerText, block.parent ?: base, process)
                }

                text.startsWith(BLOCK_START) -> {
                    mapAjaxBlock(text).map {
                        val target = block.ajaxBlockElements[it.key]
                        if (target != null) {
                            var pos1 = 0
                            if (it.value.startsWith(BLOCK_START))
                                pos1 += it.value.indexOf(':') + 1
                            var pos2 = it.value.length - pos1
                            if (it.value.endsWith(BLOCK_END))
                                pos2 -= BLOCK_END.length
                            target.d.innerHTML = it.value.substring(pos1, pos2)//.substring(it.value.indexOf(':') + 1)
                            target.refresh()
                        }
                    }
                }

                text.startsWith(OPEN_MODAL) -> {
                    trace("Helper::opening modal ...")
                    if (url != null)
                        urlStack.addLast(url)

                    if (process != null) {
                        processingStack.add(process)
                    }

                    if (text.endsWith(RELOAD)) {
                        block.modal.open(text.substring(OPEN_MODAL.length).dropLast(RELOAD.length), true)
                    } else {
                        block.modal.open(text.substring(OPEN_MODAL.length), false)
                    }
                    val s = block.modal.dModalBody.getElementsByTagName("script")
                    trace("Executing $s")
                }

                text.startsWith(REFRESH_MODAL) -> {
                    trace("Helper::refresh modal $text")
                    if (url != null) {
                        urlStack.removeLast()
                        urlStack.addLast(url)
                    }

                    if (process != null) {
                        processingStack.add(process)
                    }
                    block.modal.dModalBody.innerHTML = text
                    val s = block.modal.dModalBody.getElementsByTagName("script")
                    trace("Executing $s")
                }

                text.startsWith(REDIRECT) -> {
                    trace("Helper::redirect ${text.substring(REDIRECT.length)}")
                    location.href = text.substring(REDIRECT.length)
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
                        trace("Helper::update current block $text")
                        block.updateContent(text)
                    }
                }

            }
        }

        fun saveOrOpenBlob(blob: Blob, fileName: String) {
            trace("Helper::saveOrOpenBlob blob.size: ${blob.size}, fileName: $fileName")
            val a = document.createElement("a") as HTMLAnchorElement
            a.href = URL.createObjectURL(blob)
            a.download = fileName
            a.dispatchEvent(MouseEvent(EventType("click")))
        }

        fun hydrateStateToUrl(targetUrl: URL) {
            historyState.forEach { entry: Map.Entry<String, FormDataEntryValue> ->
                if (entry.key != "refresh" && entry.value.toString().isNotEmpty()) {
                    targetUrl.searchParams[entry.key] = entry.value.toString()
                }
            }
        }

    }
}
