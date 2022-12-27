package taack.ui.base.menu

import grails.util.Holders
import grails.web.servlet.mvc.GrailsParameterMap
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.base.TaackUiEnablerService
import taack.ui.EnumOption
import taack.ui.base.common.ActionIcon
import taack.ui.base.helper.Utils

@CompileStatic
final class MenuSpec {
    final IUiMenuVisitor menuVisitor
    final SubMenuSpec subMenuSpec

    TaackUiEnablerService taackUiEnablerService = Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') as TaackUiEnablerService

    MenuSpec(final IUiMenuVisitor menuVisitor) {
        this.menuVisitor = menuVisitor
        this.subMenuSpec = new SubMenuSpec(menuVisitor)
    }

    enum MenuMode {
        HORIZONTAL,
        VERTICAL
    }

    enum MenuPosition {
        TOP_LEFT, BOTTOM_RIGHT
    }

    void menuStart(final MenuMode menuMode) {
        menuVisitor.visitMenuStart(menuMode)
    }

    void menu(final String i18n, final MethodClosure action = null, Map<String, ? extends Object> params = null,
              @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = SubMenuSpec) final Closure closure) {
        if (closure) {
            menuVisitor.visitMenu(i18n, Utils.getControllerName(action), action?.method?.toString(), params)
            closure.delegate = subMenuSpec
            closure.call()
            menuVisitor.visitMenuEnd()
        }
    }

    void menu(final String i18n, final MethodClosure action, Map<String, ? extends Object> params = null) {
        if (taackUiEnablerService.hasAccess(action, params)) menuVisitor.visitSubMenu(i18n, Utils.getControllerName(action), action.method.toString(), params)
    }

    void menuIcon(final String i18n, final ActionIcon icon, final MethodClosure action, final boolean isModal) {
        if (taackUiEnablerService.hasAccess(action)) menuVisitor.visitSubMenuIcon(i18n, icon, Utils.getControllerName(action), action.method.toString(), null, isModal)
    }

    void menuIcon(final String i18n, final ActionIcon icon, final MethodClosure action, Map<String, ? extends Object> params = null, final boolean isModal = false) {
        if (taackUiEnablerService.hasAccess(action, params)) menuVisitor.visitSubMenuIcon(i18n, icon, Utils.getControllerName(action), action.method.toString(), params, isModal)
    }

    void menuSelect(String paramName, EnumOption[] selects, GrailsParameterMap params = null) {
        menuVisitor.visitMenuSelect(paramName, selects, params as Map)
    }

    void menuSearch(final MethodClosure action, String q) {
        menuVisitor.visitMenuSearch action, q, null
    }

    void section(final String i18n, final MenuPosition position = MenuPosition.TOP_LEFT,
                 @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure) {
        menuVisitor.visitSection(i18n, position)
        closure.delegate = subMenuSpec
        closure.call()
        menuVisitor.visitSectionEnd()
    }
}
