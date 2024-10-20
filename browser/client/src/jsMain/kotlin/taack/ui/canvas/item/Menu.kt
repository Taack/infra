package taack.ui.canvas.item

import web.canvas.CanvasRenderingContext2D


class Menu(val entries: List<MenuEntry>) {

    val font = "12px Arial"
    val fillStyle = "black"

    var posX: Double = 0.0
    var posY: Double = 0.0

    fun draw(ctx: CanvasRenderingContext2D, posX: Double, posY: Double) {
        ctx.save()
        ctx.font = font
        this.posX = posX
        this.posY = posY
        ctx.fillStyle = "lightgray"
        ctx.fillRect(posX, posY, 100.0, entries.size * 20.0 + 10.0)
        ctx.fillStyle = fillStyle
        for (i in entries.indices) {
            ctx.fillText(
                entries[i].label,
                posX + 10.0,
                posY + 20.0 * (i + 1))
        }

        ctx.restore()
    }

    fun onClick(posX: Double, posY: Double) {
        if (posX > this.posX && posX < this.posX + 100.0 && posY > this.posY && posY < this.posY + entries.size * 20.0 + 10.0) {
            val i = ((posY - this.posY) / 20.0).toInt()
            entries[i].onClick()
        }
    }
}