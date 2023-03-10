package taack.ui.base.menu

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.base.helper.Utils

@CompileStatic
final class SubMenuSpec {
    final IUiMenuVisitor menuVisitor

    SubMenuSpec(final IUiMenuVisitor menuVisitor) {
        this.menuVisitor = menuVisitor
    }

    void subMenu(final String i18n, final MethodClosure action, final Map<String, ? extends Object> params = null) {
        menuVisitor.visitSubMenu(i18n, Utils.getControllerName(action), action.method, params)
    }
}
