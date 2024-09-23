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

    /**
     * Labeled menu entry, can contain a closure to describe sub-menus
     * @param i18n  captation
     * @param closure   sub-menu listing
     */
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

    /**
     * Menu entry with explicit captation
     *
     * @param i18n
     * @param action
     * @param params
     */
    void menu(String i18n, final MethodClosure action, Map<String, ? extends Object> params = null) {
        if (taackUiEnablerService.hasAccess(action, params)) menuVisitor.visitLabeledSubMenu(i18n, Utils.getControllerName(action), action.method.toString(), params)
    }

    /**
     * Menu entry with implicit captation
     * @param action
     * @param params
     */
    void menu(final MethodClosure action, Map<String, ? extends Object> params = null) {
        if (taackUiEnablerService.hasAccess(action, params)) menuVisitor.visitLabeledSubMenu(null, Utils.getControllerName(action), action.method.toString(), params)
    }

    void menu(String i18n = null, final MethodClosure action, Long id) {
        Map params = [id: id]
        if (taackUiEnablerService.hasAccess(action, params)) menuVisitor.visitLabeledSubMenu(i18n, Utils.getControllerName(action), action.method.toString(), params)
    }

    /**
     * Menu on the right part of the screen, displaying an icon
     * @param icon
     * @param action
     * @param id
     */
    void menuIcon(final ActionIcon icon, final MethodClosure action, Long id = null) {
        Map params = [id: id]
        if (taackUiEnablerService.hasAccess(action, params)) menuVisitor.visitSubMenuIcon(null, icon, Utils.getControllerName(action), action.method.toString(), [id: id], true)
    }

    /**
     * Menu on the right part of the screen, displaying an icon
     * @param icon
     * @param action
     * @param params
     */
    void menuIcon(final ActionIcon icon, final MethodClosure action, Map<String, ? extends Object> params) {
        if (taackUiEnablerService.hasAccess(action, params)) menuVisitor.visitSubMenuIcon(null, icon, Utils.getControllerName(action), action.method.toString(), params, true)
    }

    /**
     * Menu Entry using a select box. [paramName: value] will be transmitted current action
     * @param paramName
     * @param selects
     * @param params
     */
    void menuSelect(String paramName, IEnumOptions selects, GrailsParameterMap params = null) {
        menuVisitor.visitMenuSelect(paramName, selects, params as Map)
    }

    /**
     * Display the search box
     * @param action
     * @param q
     */
    void menuSearch(final MethodClosure action, String q) {
        menuVisitor.visitMenuSearch action, q, null
    }

    /**
     * Menu Entry using a select box.
     * @param options
     */
    void menuOptions(IEnumOptions options) {
        menuVisitor.visitMenuOptions(options)
    }

}
