package taack.ui.canvas.table

import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.item.MenuEntry
import taack.ui.canvas.text.CanvasLine
import taack.ui.canvas.text.CanvasText
import web.canvas.CanvasRenderingContext2D

class CanvasTable(private val columns: Int) : ICanvasDrawable {

    private val rows = mutableListOf<CanvasText>()
    private var currentRow: CanvasText? = null
    override var globalPosYStart: Double = 0.0
    override var globalPosYEnd: Double = 0.0

    fun addCell(cell: String): CanvasTable {

        if (rows.size < columns) {
            rows.add(TxtHeaderCanvas(cell))
        } else {
            rows.add(TxtRowCanvas(cell))
        }
        return this
    }

    override fun getSelectedText(posX: Double?, posY: Double?): CanvasText? {
        if (posX == null || posY == null) {
            return this.rows.first()
        }
        for (r in rows) {
            console.log("posX: $posX, posY: $posY, r: $r")
            if (posY in r.globalPosYStart..r.globalPosYEnd && posX in r.posXStart..r.posXEnd) {
                currentRow = r
                return r
            }
        }
        return null
    }

    override fun draw(ctx: CanvasRenderingContext2D, width: Double, posY: Double, posX: Double): Double {
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
        return globalPosYEnd
    }

    override fun click(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Pair<CanvasLine, Int>? {
        for (r in rows) {
            if (r.isClicked(posX, posY)) {
                return r.click(ctx, posX, posY)
            }
        }
        return null
    }

    override fun doubleClick(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Triple<CanvasLine, Int, Int>? {
        console.log("table::doubleClick: $posX, $posY, $rows")
        for (r in rows) {
            if (r.isClicked(posX, posY)) {
                console.log("table::doubleClick2: $r, $posX, $posY")
                return r.doubleClick(ctx, posX, posY)
            }
        }
        return null

    }

    override fun getContextualMenuEntries(dblClick: Triple<CanvasLine, Int, Int>): List<MenuEntry> {
        return currentRow?.getContextualMenuEntries(dblClick) ?: emptyList()
    }


}