package taack.ui.canvas.item

import taack.ui.canvas.command.ICanvasCommand

class MenuEntry(val label: String, val onClick: () -> ICanvasCommand)