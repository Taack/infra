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

    /**
     * Level 2 menu entry
     * @param i18n
     * @param action
     * @param params
     */
    void subMenu(String i18n, final MethodClosure action, final Map<String, ? extends Object> params = null) {
        menuVisitor.visitLabeledSubMenu(i18n, Utils.getControllerName(action), action.method, params)
    }

    /**
     * Level 2 menu entry
     * @param action
     * @param params
     */
    void subMenu(final MethodClosure action, final Map<String, ? extends Object> params = null) {
        menuVisitor.visitLabeledSubMenu(null, Utils.getControllerName(action), action.method, params)
    }

    /**
     * Section allowing to group sub-menu
     * @param i18n
     * @param position
     * @param closure
     */
    void section(final String i18n, final MenuSpec.MenuPosition position = MenuSpec.MenuPosition.TOP_LEFT,
                 @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = SubMenuSpec) final Closure closure) {
        menuVisitor.visitMenuSection(i18n, position)
        closure.delegate = this
        closure.call()
        menuVisitor.visitMenuSectionEnd()
    }

}
