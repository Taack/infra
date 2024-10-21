package taack.ui.canvas.table

import taack.ui.canvas.ICanvasDrawable
import web.canvas.CanvasRenderingContext2D

class CanvasTable(val columns: Int) : ICanvasDrawable {

    private val cells = mutableListOf<CanvasCell>()

    fun addCell(cell: CanvasCell): CanvasTable {
        cells.add(cell)
        return this
    }

    fun addRow(row: List<CanvasCell>) {
        cells.addAll(row)
    }

    override fun draw(ctx: CanvasRenderingContext2D, width: Int, posY: Double): Double {
        ctx.save()
        var y = posY + 10.0
        for (i in cells.indices) {
            if (i < columns) {
                ctx.fillStyle = "#58b2eebf"
                ctx.strokeStyle = "#ffffff"
                ctx.fillRect(
                    10.0 + (i % columns).toDouble() * width / columns,
                    y + (i / columns).toInt() * 30.0,
                    width.toDouble() / columns,
                    30.0
                )

            } else ctx.fillStyle = "#11111111"
            ctx.strokeRect(
                10.0 + (i % columns).toDouble() * width / columns,
                y + (i / columns).toInt() * 30.0,
                width.toDouble() / columns,
                30.0
            )
            ctx.save()
            if (i < columns) {
                ctx.font = "bold 14px sans-serif"
                ctx.fillStyle = "#ffffff"
            }
            else {
                ctx.font = "14px sans-serif"
                ctx.fillStyle = "#555"
            }
            ctx.fillText(
                cells[i].txt,
                15.0 + (i % columns) * width / columns,
                y + (1 + i / columns).toInt() * 30.0 - 10.0,
                )
            ctx.restore()
        }
        ctx.restore()
        return y+(1 + cells.size / columns).toInt() * 30.0
    }
}