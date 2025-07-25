package taack.ui.dsl.menu

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.render.TaackUiEnablerService
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.helper.Utils

@CompileStatic
final class SubMenuSpec {
    final IUiMenuVisitor menuVisitor

    TaackUiEnablerService taackUiEnablerService = Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') as TaackUiEnablerService

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
        if (taackUiEnablerService.hasAccess(action, params)) menuVisitor.visitLabeledSubMenu(i18n, Utils.getControllerName(action), action.method, params)
    }

    /**
     * Level 2 menu entry
     * @param action
     * @param params
     */
    void subMenu(final MethodClosure action, final Map<String, ? extends Object> params = null) {
        if (taackUiEnablerService.hasAccess(action, params)) menuVisitor.visitLabeledSubMenu(null, Utils.getControllerName(action), action.method, params)
    }

    void subMenuIcon(final ActionIcon icon, final MethodClosure action, Map<String, ? extends Object> params) {
        if (taackUiEnablerService.hasAccess(action, params)) menuVisitor.visitSubMenuIcon(null, icon, Utils.getControllerName(action), action.method.toString(), params, true)
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
