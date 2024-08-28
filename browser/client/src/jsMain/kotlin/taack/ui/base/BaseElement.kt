package taack.ui.base

import taack.ui.base.element.Block

interface BaseElement : LeafElement {
    fun getParentBlock(): Block
}