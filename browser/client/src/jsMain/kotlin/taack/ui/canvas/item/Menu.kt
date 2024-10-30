package taack.ui.canvas.item

import taack.ui.base.Helper.Companion.trace
import taack.ui.canvas.command.ICanvasCommand
import web.canvas.CanvasRenderingContext2D


class Menu(private val entries: List<MenuEntry>) {

    private val font = "12px Arial"
    val fillStyle = "black"

    private var posX: Double = 0.0
    private var posY: Double = 0.0

    fun draw(ctx: CanvasRenderingContext2D, posX: Double, posY: Double) {
        trace("Menu::draw: $posX, $posY")
        ctx.save()
        ctx.font = font
        this.posX = posX
        this.posY = posY
        ctx.fillStyle = "lightgray"
        ctx.fillRect(posX, posY, 150.0, entries.size * 20.0 + 10.0)
        ctx.fillStyle = fillStyle
        for (i in entries.indices) {
            ctx.fillText(
                entries[i].label,
                posX + 10.0,
                posY + 20.0 * (i + 1))
        }

        ctx.restore()
    }

    fun onClick(posX: Double, posY: Double): ICanvasCommand? {
        trace("Menu::onClick: $posX, $posY")
        if (posX > this.posX && posX < this.posX + 100.0 && posY > this.posY && posY < this.posY + entries.size * 20.0 + 10.0) {
            val i = ((posY - this.posY) / 20.0).toInt()
            return entries[i].onClick()
        }
        return null
    }
}