package taack.ui.base

interface BaseAjaxElement : BaseElement {
    companion object fun findAll(p : BaseAjaxElement): List<BaseAjaxElement>
    fun textContains(s: String) : Boolean
    fun popElement(s: String) : Pair<BaseAjaxElement, String>
}