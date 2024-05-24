package taack.ui.dump.theme.elements.form

import taack.ui.dump.theme.elements.base.IHTMLElement

trait IFormThemed implements IHTMLElement {
    abstract IHTMLElement enumInput()
    abstract IHTMLElement booleanInput()
    abstract IHTMLElement listOrSetInput()
    abstract IHTMLElement dateInput()
    abstract IHTMLElement textareaInput()
    abstract IHTMLElement fileInput()
    abstract IHTMLElement normalInput()
    abstract IHTMLElement formSection(IHTMLElement inner)
    abstract IHTMLElement formTabs(IHTMLElement inner)
    abstract IHTMLElement formTab(IHTMLElement inner)
    abstract IHTMLElement formCol(IHTMLElement inner)
    abstract IHTMLElement formAction()
}