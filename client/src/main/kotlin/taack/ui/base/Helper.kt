package taack.ui.base

import kotlinx.browser.window
import org.w3c.dom.asList
import taack.ui.base.element.Block

typealias CloseModalPostProcessing = ((String, String, Map<String, String>) -> Unit)

class Helper {
    companion object {
        var level = 0
        var hasExecutedAjax = false
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

        fun mapAjaxText(text: String): Map<String, String> {
            console.log("Mapping Ajax Content ... ${text.substring(0, 10)}")
            val m = mutableMapOf<String, String>()
            val abs = "__ajaxBlockStart__"
            val abe = "__ajaxBlockEnd__"
            if (text.startsWith(abs)) {
                hasExecutedAjax = true
                var pos1 = abs.length
                var pos2 = text.indexOf(':')
                do {
                    val abId = text.substring(pos1, pos2)
                    pos1 = text.indexOf(abe, pos2)
                    val content = text.substring(pos2 + 1, pos1)
                    pos1 += abe.length
                    pos1 += abs.length
                    pos2 = text.indexOf(':', pos1)

                    m[abId] = content
                } while (pos2 != -1)
            }
            return m
        }

        val processingStack: ArrayDeque<CloseModalPostProcessing> = ArrayDeque()

        fun processAjaxLink(text: String, block: Block, process: CloseModalPostProcessing? = null) {
            val abs = "__ajaxBlockStart__"
            val m = "closeLastModal:"
            val m2 = "closeLastModalAndUpdateBlock:"
            val fi = ":__FieldInfo__:"
            val fie = ":__FieldInfoEnd__"
            val rel = "__reload__"
            when {
                text.contains(rel) -> {
                    window.location.href = (Block.href ?: "")// + "?recordState=${RecordState.dumpServerState()}"
                }
                text.startsWith(m) -> {
                    val pos = text.indexOf(':', m.length)
                    if (text[m.length] != ':' || text.subSequence(text.length - fie.length, text.length) == fie) {
                        var posField = text.indexOf(fi)
                        if (processingStack.isNotEmpty()) {
                            trace("Helper::process")
                            val id = text.substring(m.length, pos)
                            val value = if (posField == -1) text.substring(pos + 1) else text.substring(pos + 1, posField)
                            var otherField = emptyMap<String, String>()
                            while (posField != -1) {
                                val endFieldNameIndex = text.indexOf(':', posField + fi.length)
                                val fieldName = text.substring(posField + fi.length, endFieldNameIndex)
                                val endFieldValueIndex = text.indexOf(fie, endFieldNameIndex)
                                val fieldValue = text.substring(endFieldNameIndex + 1, endFieldValueIndex)
                                otherField = otherField.plus(Pair(fieldName, fieldValue))
                                posField = text.indexOf(fi, endFieldValueIndex)
                            }
                            val f = processingStack.last()
                            f(id, value, otherField)
                        }
                    } else {
                        if (text.length > m.length + 1 && text.substring(m.length + 1).startsWith(abs)) {
                            mapAjaxText(text.substring(m.length + 1)).map {
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
                text.startsWith(m2) -> {
                    if (block.parent != null) block.parent.close()
                    else block.modal.close()
                    if (text.substring(29).startsWith(abs)) {
                        mapAjaxText(text.substring(29)).map {
                            val target = block.ajaxBlockElements?.get(it.key) ?: block.parent!!.parent.ajaxBlockElements!![it.key]
                            target!!.d.innerHTML = it.value
                            target.refresh()
                        }
                    } else if (text[29] == '<') {
//                        block.modal.open(text.substring(29))
                        if (block.parent != null) block.parent.open(text.substring(29))
                        else block.modal.open(text.substring(29))

                    } else if (text.substring(29) == rel) {
                        println("RELOAD")
                        window.location.href = Block.href ?: ""
                    }
                }
                text.startsWith(abs) -> {
                    mapAjaxText(text).map {
                        val target = block.ajaxBlockElements?.get(it.key)
                        target!!.d.innerHTML = it.value
                        target.refresh()
                    }
                }
                else -> {
                    trace("Helper::opening Modal")
                    hasExecutedAjax = true
                    if (process != null) {
                        processingStack.add(process)
                    }
                    block.modal.open(text)
                    val s = block.modal.innerModal.getElementsByTagName("script").asList()
                    trace("Executing $s")

//                  See ajaxBlock ..
//                    s.map {
//                        trace("Evaluating ${it.innerHTML}")
//                        eval(it.innerHTML)
//                    }
//
//                    if (s != null) {
//                        eval(s.innerHTML)
//                    }
                }
            }

        }
    }
}