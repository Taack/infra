package taack.ui.dsl.menu

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.dsl.helper.Utils

@CompileStatic
final class SubMenuSpec {
    final IUiMenuVisitor menuVisitor

    SubMenuSpec(final IUiMenuVisitor menuVisitor) {
        this.menuVisitor = menuVisitor
    }

    void subMenu(final MethodClosure action, final Map<String, ? extends Object> params = null) {
        menuVisitor.visitSubMenu(Utils.getControllerName(action), action.method, params)
    }
}
