package taack.ui.base.leaf

import js.array.asList
import kotlinx.browser.document
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.checkLogin
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.LeafElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block
import taack.ui.base.element.Tab
import taack.ui.base.element.TableRowForm
import web.cssom.ClassName
import web.dom.Element
import web.events.Event
import web.events.EventHandler
import web.form.FormData
import web.html.*
import web.http.POST
import web.http.RequestMethod
import web.location.location
import web.parsing.DOMParser
import web.parsing.DOMParserSupportedType
import web.parsing.textHtml
import web.url.URL
import web.xhr.XMLHttpRequest
import kotlin.math.min

class TableRowFormButton(val parent: TableRowForm, val b: HTMLInputElement) : LeafElement  {
    companion object {
        fun getSiblingTableRowFormButton(p: TableRowForm): List<TableRowFormButton> {
            val elements: List<*> = p.parent.r.querySelectorAll("input[type='submit']").asList()
            return elements.map {
                TableRowFormButton(p, it as HTMLInputElement)
            }
        }
    }

    init {
        trace("TableRowFormButton::init ${b.formAction}")
        b.onclick = EventHandler { e ->
            onClick(e)
        }
    }

    private fun onClick(e: Event) {
        b.disabled = true
        e.preventDefault()
        trace("TableRowFormButton::onclick: ${parent.f.action}")
        val f = parent.f
        val fd = FormData(f)
        fd.append("isAjax", "true")
        val xhr = XMLHttpRequest()
        xhr.onloadend = EventHandler {
            checkLogin(xhr)
            b.disabled = false
            val t = xhr.responseText
            if (t.substring(0, min(20, t.length)).contains("<!DOCTYPE html>", false)) {
                location.href = parent.f.action
                document.write(t)
                document.close()
            }
        }
        xhr.open(RequestMethod.POST, parent.f.action)
        xhr.send(fd)
    }
}