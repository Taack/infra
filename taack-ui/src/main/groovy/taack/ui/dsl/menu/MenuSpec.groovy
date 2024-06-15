package taack.ui.dsl.menu

import grails.util.Holders
import grails.web.servlet.mvc.GrailsParameterMap
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.render.TaackUiEnablerService
import taack.ui.IEnumOptions
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.helper.Utils

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

    void label(final String i18n, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = SubMenuSpec) final Closure closure = null) {
        if (closure) {
            menuVisitor.visitMenuLabel(i18n, true)
            closure.delegate = subMenuSpec
            closure.call()
            menuVisitor.visitMenuLabelEnd()
        } else {
            menuVisitor.visitMenuLabel(i18n, false)
        }
    }

    void menu(final MethodClosure action, Map<String, ? extends Object> params = null) {
        if (taackUiEnablerService.hasAccess(action, params)) menuVisitor.visitMenu(Utils.getControllerName(action), action.method.toString(), params)
    }

    void menu(final MethodClosure action, Long id) {
        Map params = [id: id]
        if (taackUiEnablerService.hasAccess(action, params)) menuVisitor.visitSubMenu(Utils.getControllerName(action), action.method.toString(), params)
    }

    void menuIcon(final ActionIcon icon, final MethodClosure action, Long id = null) {
        if (taackUiEnablerService.hasAccess(action)) menuVisitor.visitSubMenuIcon(null, icon, Utils.getControllerName(action), action.method.toString(), [id: id], true)
    }

    void menuIcon(final ActionIcon icon, final MethodClosure action, Map<String, ? extends Object> params) {
        if (taackUiEnablerService.hasAccess(action, params)) menuVisitor.visitSubMenuIcon(null, icon, Utils.getControllerName(action), action.method.toString(), params, true)
    }

    void menuSelect(String paramName, IEnumOptions selects, GrailsParameterMap params = null) {
        menuVisitor.visitMenuSelect(paramName, selects, params as Map)
    }

    void menuSearch(final MethodClosure action, String q) {
        menuVisitor.visitMenuSearch action, q, null
    }

    void menuOptions(IEnumOptions options) {
        menuVisitor.visitMenuOptions(options)
    }

    void section(final String i18n, final MenuPosition position = MenuPosition.TOP_LEFT,
                 @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = SubMenuSpec) final Closure closure) {
        menuVisitor.visitMenuSection(i18n, position)
        closure.delegate = subMenuSpec
        closure.call()
        menuVisitor.visitMenuSectionEnd()
    }
}
