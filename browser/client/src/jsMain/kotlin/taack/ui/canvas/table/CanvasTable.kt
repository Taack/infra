package taack.ui.canvas.table

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.item.MenuEntry
import taack.ui.canvas.text.CanvasLine
import taack.ui.canvas.text.CanvasText
import web.canvas.CanvasRenderingContext2D

class CanvasTable(private var columns: Int) : ICanvasDrawable {

    private val rows = mutableListOf<CanvasText>()
    private var currentRow: CanvasText? = null
    override var globalPosYStart: Double = 0.0
    override var globalPosYEnd: Double = 0.0

    companion object {
        fun createTable() = CanvasTable(3)
            .addCell("Header 1").addCell("Header 2").addCell("Header 3")
            .addCell("Cell 1").addCell("Cell 2").addCell("Cell 3")
    }

    fun addCell(cell: String): CanvasTable {
        trace("CanvasTable::addCell")
        if (rows.size < columns) {
            rows.add(TxtHeaderCanvas(cell))
        } else {
            rows.add(TxtRowCanvas(cell))
        }
        return this
    }

    override fun getSelectedText(posX: Double?, posY: Double?): CanvasText {
        trace("CanvasTable::getSelectedText")
        if (posX == null || posY == null) {
            return this.rows.first()
        }
        for (r in rows) {
            if (posY in r.globalPosYStart..r.globalPosYEnd && posX in r.posXStart..r.posXEnd) {
                currentRow = r
                return r
            }
        }
        currentRow = rows.last()
        if (posY < currentRow!!.globalPosYEnd) {
            currentRow = rows.first()
        }
        return currentRow!!
    }

    override fun draw(ctx: CanvasRenderingContext2D, width: Double, posY: Double, posX: Double): Double {
        traceIndent("CanvasTable::draw: $posX, $posY, $width")
        ctx.save()
        globalPosYStart = posY
        val y = posY + 10.0
        for (i in (rows.indices)) {
            if (i < columns) {
                ctx.save()
                ctx.fillStyle = "#58b2eebf"
                ctx.strokeStyle = "#ffffff"
                ctx.fillRect(
                    10.0 + (i % columns).toDouble() * width / columns,
                    y + (i / columns) * 30.0,
                    width / columns,
                    30.0
                )
            }
            ctx.save()
            ctx.fillStyle = "#11111111"
            ctx.strokeRect(
                10.0 + (i % columns).toDouble() * width / columns,
                y + (i / columns) * 30.0,
                width / columns,
                30.0
            )
            ctx.restore()
            rows[i].draw(
                ctx,
                (i % columns + 1) * width / columns,
                y + (i / columns) * 30.0,
                20.0 + (i % columns).toDouble() * width / columns
            )

        }
        ctx.restore()
        globalPosYEnd = y + (1 + (rows).size / columns) * 30.0
        traceDeIndent("CanvasTable::draw: $globalPosYEnd")
        return globalPosYEnd
    }

    override fun click(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Pair<CanvasLine, Int>? {
        traceIndent("CanvasTable::click: $posX, $posY")
        for (r in rows) {
            if (r.isClicked(posX, posY)) {
                traceDeIndent("CanvasTable::click: $r, $posX, $posY")
                return r.click(ctx, posX, posY)
            }
        }
        traceDeIndent("CanvasTable::click: null")
        return null
    }

    override fun doubleClick(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Triple<CanvasLine, Int, Int>? {
        traceIndent("CanvasTable::doubleClick: $posX, $posY")
        for (r in rows) {
            if (r.isClicked(posX, posY)) {
                traceDeIndent("CanvasTable::doubleClick: $r, $posX, $posY")
                return r.doubleClick(ctx, posX, posY)
            }
        }
        traceDeIndent("CanvasTable::doubleClick: null")
        return null

    }

    override fun getContextualMenuEntries(dblClick: Triple<CanvasLine, Int, Int>): List<MenuEntry> {
        trace("CanvasTable::getContextualMenuEntries: $dblClick")
        return currentRow?.getContextualMenuEntries(dblClick) ?: emptyList()
    }

    fun addLine(currentText: TxtRowCanvas) {
        for (i in (rows.indices)) {
            if (rows[i] == currentText) {
                for (j in (0 until columns)) {
                    rows.add((i - (i%columns) + columns), TxtRowCanvas(">"))
                }
                break
            }
        }
    }

    fun removeLine(currentText: TxtRowCanvas) {
        if (rows.size <= 2*columns) return
        for (i in (rows.indices)) {
            if (rows[i] == currentText) {
                for (j in (0 until columns)) {
                    rows.removeAt(i - (i%columns) + columns)
                }
                break
            }
        }
    }

    fun addColumn(currentText: TxtHeaderCanvas) {
        for (i in (rows.indices)) {
            if (rows[i] == currentText) {
                rows.add(i + 1, TxtHeaderCanvas(">"))
                columns += 1
                for (j in ((columns + i + 1)  until rows.size step columns)) {
                    rows.add(j, TxtRowCanvas(">"))
                }
                break
            }
        }
    }

    fun removeColumn(currentText: TxtHeaderCanvas) {
        if (rows.size <= 4) return
        for (i in (rows.indices)) {
            if (rows[i] == currentText) {
                rows.removeAt(i)
                columns -= 1
                for (j in ((columns + i)  until rows.size step columns)) {
                    rows.removeAt(j)
                }
                break
            }
        }
    }

}