package taack.ui.canvas.table

import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.item.MenuEntry
import taack.ui.canvas.text.CanvasLine
import taack.ui.canvas.text.CanvasText
import web.canvas.CanvasRenderingContext2D

class CanvasTable(private val columns: Int) : ICanvasDrawable {

    val header = mutableListOf<CanvasCellHeader>()
    val rows = mutableListOf<CanvasCellRow>()

    fun addCell(cell: CanvasCellHeader): CanvasTable {
        header.add(cell)
        return this
    }

    fun addCell(cell: CanvasCellRow): CanvasTable {
        rows.add(cell)
        return this
    }

    override var globalPosYStart: Double = 0.0
    override var globalPosYEnd: Double = 0.0
    override fun getSelectedText(posX: Double, posY: Double): CanvasText? {
        TODO("Not yet implemented")
    }

    override fun draw(ctx: CanvasRenderingContext2D, width: Int, posY: Double): Double {
        ctx.save()
        globalPosYStart = posY
        val y = posY + 10.0
        for (i in (header.indices)) {
            ctx.fillStyle = "#58b2eebf"
            ctx.strokeStyle = "#ffffff"
            ctx.fillRect(
                10.0 + (i % columns).toDouble() * width / columns,
                y + (i / columns) * 30.0,
                width.toDouble() / columns,
                30.0
            )
            ctx.strokeRect(
                10.0 + (i % columns).toDouble() * width / columns,
                y + (i / columns) * 30.0,
                width.toDouble() / columns,
                30.0
            )
            ctx.save()
                ctx.font = "bold 14px sans-serif"
                ctx.fillStyle = "#ffffff"
            ctx.fillText(
                header[i].txt.txt,
                15.0 + (i % columns) * width / columns,
                y + (1 + i / columns) * 30.0 - 10.0,
            )
            ctx.restore()
        }
        for (i in rows.indices) {
            if (i < columns) {
                ctx.fillStyle = "#58b2eebf"
                ctx.strokeStyle = "#ffffff"
                ctx.fillRect(
                    10.0 + (i % columns).toDouble() * width / columns,
                    y + (i / columns) * 30.0,
                    width.toDouble() / columns,
                    30.0
                )

            } else ctx.fillStyle = "#11111111"
            ctx.strokeRect(
                10.0 + (i % columns).toDouble() * width / columns,
                y + (i / columns) * 30.0,
                width.toDouble() / columns,
                30.0
            )
            ctx.save()
            if (i < columns) {
                ctx.font = "bold 14px sans-serif"
                ctx.fillStyle = "#ffffff"
            } else {
                ctx.font = "14px sans-serif"
                ctx.fillStyle = "#555"
            }
            ctx.fillText(
                rows[i].txt.txt,
                15.0 + (i % columns) * width / columns,
                y + (1 + i / columns) * 30.0 - 10.0,
            )
            ctx.restore()
        }
        ctx.restore()
        globalPosYEnd = y + (1 + (header + rows).size / columns) * 30.0
        return globalPosYEnd
    }

    override fun click(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Pair<CanvasLine, Int>? {
        TODO("Not yet implemented")
    }

    override fun doubleClick(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Triple<CanvasLine, Int, Int>? {
        TODO("Not yet implemented")
    }

    override fun getContextualMenuEntries(dblClick: Triple<CanvasLine, Int, Int>): List<MenuEntry> {
        TODO("Not yet implemented")
    }


}