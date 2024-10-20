package taack.ui.canvas

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import taack.ui.canvas.item.CanvasCaret
import taack.ui.canvas.text.*

class MainCanvas(private val divHolder: HTMLDivElement, private val divScroll: HTMLDivElement) {
    val canvas: HTMLCanvasElement = document.createElement("canvas") as HTMLCanvasElement
    private val ctx: CanvasRenderingContext2D = canvas.getContext("2d") as CanvasRenderingContext2D
    private val texts = mutableListOf<CanvasText>()
    private val divContainer: HTMLDivElement = document.createElement("div") as HTMLDivElement
    private var dy: Double = 0.0
    private var caretPosInCurrentText: Int = 0
    private var currentText: CanvasText? = null
    private var currentLine: CanvasLine? = null
    private var currentMouseEvent: MouseEvent? = null
    private var currentKeyboardEvent: KeyboardEvent? = null
    private var charOffset: Int = 0
    private var wordOffset: Int = 0
    private var lineOffset: Int = 0
    private var clickYPosBefore: Double = 0.0
    private var isDoubleClick: Boolean = false
    private var charSelectStartNInText: Int = 0
    private var charSelectStopNInText: Int = 0

    private fun addText() {
        when (currentKeyboardEvent!!.key) {
            "Backspace" -> {
                if (currentText?.rmChar(caretPosInCurrentText + charOffset) == 0) {
                    val i = texts.indexOf(currentText!!)
                    texts.remove(currentText!!)
                } else
                    charOffset--
            }

            "Delete" -> {
                if (currentText?.delChar(caretPosInCurrentText + charOffset) == 0) {
                    val i = texts.indexOf(currentText!!)
                    texts.remove(currentText!!)
                }
            }

            "Enter" -> {
                val i = texts.indexOf(currentText!!) + 1
                if (currentText is H2Canvas) {
                    currentText = H3Canvas()
                    texts.add(i, currentText as H3Canvas)
                } else if (currentText is H3Canvas) {
                    currentText = H4Canvas()
                    texts.add(i, currentText as H4Canvas)
                } else {
                    currentText = PCanvas()
                    texts.add(i, currentText as PCanvas)
                }
                currentLine = null
                caretPosInCurrentText = 0
                charOffset = 0
            }

            "ArrowUp" -> {
                if (currentLine == null) {
                    val j = texts.indexOf(currentText) - 1
                    console.log("ArrowUp => Previous Text")
                    if (j >= 0) {
                        currentText = texts[j]
                        currentLine = currentText!!.lines.last()
                    }
                } else {
                    val i = currentText!!.findLine(currentLine!!)
                    console.log("ArrowUp +++ $i $lineOffset ${currentText!!.lines}")
                    if (i > 0 && i < currentText!!.lines.size) {
                        caretPosInCurrentText -= currentLine!!.posBegin
                        currentLine = currentText!!.lines[i - 1]
                        caretPosInCurrentText += currentLine!!.posBegin
                        console.log("ArrowUp => Previous Line $currentLine")
                    } else {
                        val j = texts.indexOf(currentText) - 1
                        console.log("ArrowUp => Previous Text")
                        if (j >= 0) {
                            currentText = texts[j]
                            currentLine = currentText!!.lines.last()
                        }
                    }
                }
            }

            "ArrowDown" -> {
                if (currentLine == null) {
                    val j = texts.indexOf(currentText) + 1
                    console.log("ArrowDown => Next Text")
                    if (j > 0 && j < texts.size) {
                        currentText = texts[j]
                        currentLine = currentText!!.lines.first()
                    }
                } else {
                    val i = currentText!!.findLine(currentLine!!)
                    console.log("ArrowDown $i $lineOffset $currentLine ${currentText!!.lines}")

                    if (i >= 0 && i < currentText!!.lines.size - 1) {
                        caretPosInCurrentText -= currentLine!!.posBegin
                        currentLine = currentText!!.lines[i + 1]
                        caretPosInCurrentText += currentLine!!.posBegin
                        console.log("ArrowDown => Next Line $currentLine")
                    } else {
                        val j = texts.indexOf(currentText) + 1
                        console.log("ArrowDown => Next Text")
                        if (j > 0 && j < texts.size) {
                            currentText = texts[j]
                            currentLine = currentText!!.lines.first()
                        }
                    }
                }
            }

            "ArrowRight" -> {
                if (currentKeyboardEvent!!.ctrlKey && isDoubleClick) {
                    val decay = currentText!!.txt.substring(charSelectStopNInText + 1).indexOf(' ') + 1
                    if (decay == 0) {
                        charSelectStopNInText = currentText!!.txt.length
                    }
                    charSelectStopNInText += decay
                } else {
                    charOffset++
                }
            }

            "F2" -> {
                if (currentText != null && currentLine != null) {
                    val i = texts.indexOf(currentText!!)
                    texts.remove(currentText!!)
                    val h2 = H2Canvas()
                    h2.txt = currentText!!.txt
                    texts.add(i, h2)
                }
            }

            "F3" -> {
                if (currentText != null && currentLine != null) {
                    val i = texts.indexOf(currentText!!)
                    texts.remove(currentText!!)
                    val h3 = H3Canvas()
                    h3.txt = currentText!!.txt
                    texts.add(i, h3)
                }
            }

            "F4" -> {
                if (currentText != null && currentLine != null) {
                    val i = texts.indexOf(currentText!!)
                    texts.remove(currentText!!)
                    val h4 = H4Canvas()
                    h4.txt = currentText!!.txt
                    texts.add(i, h4)
                }
            }

            "F1" -> {
                if (currentText != null && currentLine != null) {
                    val i = texts.indexOf(currentText!!)
                    texts.remove(currentText!!)
                    val p = PCanvas()
                    p.txt = currentText!!.txt
                    texts.add(i, p)
                }
            }

            "F5" -> {
                if (currentText != null && currentLine != null) {
                    val i = texts.indexOf(currentText!!)
                    if (i != -1) {
                        texts.remove(currentText!!)
                        val p = LiCanvas()
                        p.txt = currentText!!.txt
                        texts.add(i, p)
                    }
                }
            }

            "F6" -> {
                if (currentText != null && currentLine != null) {
                    val i = texts.indexOf(currentText!!)
                    if (i != -1) {
                        texts.remove(currentText!!)
                        val p = Li2Canvas()
                        p.txt = currentText!!.txt
                        texts.add(i, p)
                    }
                }
            }

            "End" -> {
                charOffset = 0
                if (currentKeyboardEvent!!.ctrlKey) {
                    if (currentKeyboardEvent!!.shiftKey) {
                        currentText = texts.last()
                    }
                    currentLine = currentText!!.lines.last()
                }
                caretPosInCurrentText = currentLine!!.posEnd
            }

            "Home" -> {
                charOffset = 0
                if (currentKeyboardEvent!!.ctrlKey) {
                    if (currentKeyboardEvent!!.shiftKey) {
                        currentText = texts.first()
                    }
                    currentLine = currentText!!.lines.first()
                }
                caretPosInCurrentText = currentLine!!.posBegin
            }

            "Shift", "ShiftLeft", "ShiftRight", "Control", "ControlLeft", "ControlRight" -> {

            }

            "ArrowLeft" -> {
                charOffset--
            }

            else -> {
                if (currentKeyboardEvent != null)
                    currentText?.addChar(currentKeyboardEvent!!.key[0], caretPosInCurrentText + charOffset++)
            }
        }

        draw()
    }

    private fun addText(text: CanvasText) {
        texts.add(text)
    }

    private fun logMouseEvent(ev: MouseEvent) {
        console.log("logMouseEvent", ev)
    }

    private fun logKeyEvent(ev: KeyboardEvent) {
        console.log("logKeyEvent", ev)
    }

    init {
        canvas.id = "canvas"
        canvas.width = window.innerWidth
        canvas.height = window.innerHeight
        canvas.tabIndex = 1
        divContainer.appendChild(canvas)
        divHolder.appendChild(canvas)

        divScroll.onscroll = {
            dy = divScroll.scrollTop
            divHolder.style.transform = "translate(0px, ${dy}px)"
            console.log("Scroll $dy", it)
            isDoubleClick = false
            draw()
            dy
        }

        window.onresize = {
            console.log("onresize ${document.body!!.offsetWidth}")
            canvas.width = document.body!!.offsetWidth
            canvas.height = window.innerHeight - 10
            CanvasText.globalPosY = -dy
            isDoubleClick = false
            draw()
        }

        canvas.onclick = { event: MouseEvent ->
            logMouseEvent(event)
            charOffset = 0
            isDoubleClick = false

//            currentText = null
            currentMouseEvent = event
            event.preventDefault()
            event.stopPropagation()
            for (text in texts) {
                for (line in text.lines) {
                    if (event.offsetY in line.textY - line.height..line.textY) {
                        clickYPosBefore = line.textY
                        currentLine = line
                        currentText = text
                        caretPosInCurrentText = line.caretNCoords(ctx, text, event.offsetX)
                        console.log(
                            "find text line ... at (${event.offsetX}, ${event.offsetY})(${line.textY + line.height}) = ${
                                text.txt.substring(
                                    line.posBegin, caretPosInCurrentText

                                )
                            }"
                        )
                        break
                    }
                }
            }
            draw()
        }

        canvas.onmousemove = { event: MouseEvent ->
            if (event.buttons.toInt() != 0)
                console.log("onmousemove ${event.button} ${event.buttons} ${event.clientX} ${event.clientY} ${event.offsetX} ${event.offsetY} ${event.pageX} ${event.pageY} ${event.ctrlKey} ${event.altKey} ${event.shiftKey} ${event.metaKey}")
        }

        canvas.onkeydown = { event: KeyboardEvent ->
            logKeyEvent(event)
            currentKeyboardEvent = event
            if (!event.ctrlKey) isDoubleClick = false

            addText()
            event.preventDefault()
            event.stopPropagation()

        }

        canvas.ondblclick = { event: MouseEvent ->
            logMouseEvent(event)
            event.preventDefault()
            event.stopPropagation()
            isDoubleClick = true
            wordOffset = 0
            for (text in texts) {
                for (line in text.lines) {
                    if (event.offsetY in line.textY - line.height..line.textY) {
                        clickYPosBefore = line.textY
                        currentLine = line
                        currentText = text
                        caretPosInCurrentText = line.caretNCoords(ctx, text, event.offsetX)
                        charSelectStartNInText = currentText!!.txt.substring(currentLine!!.posBegin, caretPosInCurrentText).indexOfLast { it == ' ' } + 1
                        charSelectStartNInText += currentLine!!.posBegin
                        charSelectStopNInText = currentText!!.txt.substring(caretPosInCurrentText + 1).indexOfFirst { it == ' ' }
                        charSelectStopNInText += caretPosInCurrentText + 1
                        console.log(
                            "click find text line ... at (${event.offsetX}, ${event.offsetY})(${line.textY + line.height}) = ${
                                text.txt.substring(
                                    line.posBegin, caretPosInCurrentText
                                )
                            }")
                        console.log(
                            "double click find text line ... at (${charSelectStartNInText}, ${charSelectStopNInText}) = ${
                                text.txt.substring(
                                    charSelectStartNInText, charSelectStopNInText
                                )
                            }"
                        )
                        break
                    }
                }
            }
            draw()
        }
        addInitialTexts()
        draw()
    }

    private fun addInitialTexts() {
        console.log("canvas $canvas")
        console.log("ctx $ctx")

        val h2 = H2Canvas()

        h2.txt = "Topology Filters and Selectors Example for various data layout"
        addText(h2)

        val h3 = H3Canvas()
        h3.txt = "Directed Acyclic Graphs (the most common in computer sciences)"
        addText(h3)

        val p1 = PCanvas()
        p1.txt =
            "DSL are AI friendly, so we want to be able to use more natural language in the future to generate our assets, but generation will be translated into those DSLs, in order to be human editable, efficiently."
        addText(p1)

        val h31 = H3Canvas()
        h31.txt = "For Assemblies and bodies"
        addText(h31)

        val h4 = H4Canvas()
        h4.txt = "Category"
        addText(h4)

        val p2 = PCanvas()
        p2.txt = "Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        addText(p2)
        val p3 = PCanvas()
        p3.txt = p2.txt
        addText(p3)
        val p4 = PCanvas()
        p4.txt = p2.txt

        addText(p4)
    }

    private fun draw() {
        CanvasText.num1 = 0
        CanvasText.num2 = 0
        CanvasText.globalPosY = -dy

        console.log("draw +++ CanvasText.globalPosY = ${CanvasText.globalPosY}")

        ctx.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble());

        for (text in texts) {
            text.draw(ctx, canvas.width)
        }
        console.log("draw text done")

        if (currentText != null) {
            if (currentLine != null) {
                val caretPosInLine = caretPosInCurrentText - currentLine!!.posBegin
                CanvasCaret.draw(ctx, currentText!!, currentLine!!, caretPosInLine + charOffset)
                if (isDoubleClick)
                    CanvasCaret.drawDblClick(ctx, currentText!!, currentLine!!, caretPosInLine + charOffset, charSelectStartNInText, charSelectStopNInText)

            } else {
                CanvasCaret.draw(ctx, currentText!!, currentText!!.lines.first(), caretPosInCurrentText + charOffset)
                if (isDoubleClick)
                    CanvasCaret.drawDblClick(ctx, currentText!!, currentText!!.lines.first(), caretPosInCurrentText + charOffset, charSelectStartNInText, charSelectStopNInText)

                currentLine = currentText!!.lines.first()
            }
        }

        console.log("draw --- CanvasText.globalPosY = ${CanvasText.globalPosY} $caretPosInCurrentText $charOffset")
        divHolder.style.height = "${CanvasText.globalPosY + dy}px"
    }
}