package taack.ui.dsl

import groovy.transform.CompileStatic
import taack.ui.dsl.menu.IUiMenuVisitor
import taack.ui.dsl.menu.MenuSpec

@CompileStatic
final class UiMenuSpecifier {
    Closure closure

    UiMenuSpecifier ui(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = MenuSpec) Closure closure) {
        this.closure = closure
        this
    }

    void visitMenu(final IUiMenuVisitor menuVisitor) {
        if (menuVisitor && closure) {
            menuVisitor.visitMenuStart(null)
            closure.delegate = new MenuSpec(menuVisitor)
            closure.call()
            menuVisitor.visitMenuStartEnd()
        }
    }
}
