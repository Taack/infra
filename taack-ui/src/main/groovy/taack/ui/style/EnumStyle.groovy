package taack.ui.style

import taack.ui.base.common.Style

trait EnumStyle {
    String name
    Style style

    String getName() {
        return name
    }

    Style getStyle() {
        return style
    }
}