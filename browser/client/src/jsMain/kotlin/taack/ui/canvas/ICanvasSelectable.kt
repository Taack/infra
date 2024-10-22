package taack.ui.canvas

import taack.ui.canvas.item.MenuEntry
import taack.ui.canvas.text.CanvasLine
import web.canvas.CanvasRenderingContext2D

interface ICanvasSelectable {
    fun click(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Pair<CanvasLine, Int>?
    fun doubleClick(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Triple<CanvasLine, Int, Int>?
    fun getContextualMenuEntries(dblClick: Triple<CanvasLine, Int, Int>): List<MenuEntry>
}