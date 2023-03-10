package taack.ui.dump

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ui.EnumOption
import taack.ui.base.common.ActionIcon
import taack.ui.base.helper.Utils
import taack.ui.base.menu.IUiMenuVisitor
import taack.ui.base.menu.MenuSpec

@CompileStatic
final class RawHtmlMenuDump implements IUiMenuVisitor {
    final private ByteArrayOutputStream out
    final private String modalId
    final Parameter parameter

    private int menuLevel = 0

    RawHtmlMenuDump(final ByteArrayOutputStream out, final String modalId, final Parameter parameter) {
        this.out = out
        this.modalId = modalId
        this.parameter = parameter
    }

    @Override
    void visitMenuStart(MenuSpec.MenuMode menuMode) {
//        out << htmlTheme.menuStartHeader()
//        menuMode
    }

    @Override
    void visitMenuStartEnd() {
//        out << htmlTheme.menuStartFooter()
    }


    @Override
    void visitMenu(String i18n, String controller, String action, Map<String, ? extends Object> params) {
        if (controller && action && params) {
            out << """
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false" href="${parameter.urlMapped(controller, action, params)}">${i18n}</a>
                <ul class="dropdown-menu">
            """
        } else {
            out << """
             <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">${i18n}</a>
                <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
            """
        }
    }

    @Override
    void visitMenuEnd() {
        out << "</ul></li>"

    }

    @Override
    void visitSubMenu(String i18n, String controller, String action, Map<String, ? extends Object> params) {
        out << """
            <li class="nav-item dropdown">
                <a class="nav-link" href="${parameter.urlMapped(controller, action, params)}">${i18n}</a>
            </li>
        """

    }

    @Override
    void visitSection(String i18n, MenuSpec.MenuPosition position) {
        out << """
            <li class="nav-item dropdown">
                <span class="navbar-text">
                  <b>${i18n}</b>
                </span>
            </li>
        """
    }

    @Override
    void visitSectionEnd() {

    }

    @Override
    void visitSubMenuIcon(String i18n, ActionIcon actionIcon, String controller, String action, Map<String, ?> params, boolean isModal = false) {
        if (isModal)
            out << """
                 <li class='pure-menu-item' style='float: right;'>
                    <a class='taackAjaxMenuLink pure-menu-link' ajaxAction='${parameter.urlMapped(controller, action, params, true)}' }>
                        ${actionIcon.getHtml(i18n, 24)}
                    </a>
                 </li>
            """
        else
            out << """
                <li class="pure-menu-item" style='float: right;'>
                    <a class="pure-menu-link" href="${parameter.urlMapped(controller, action, params)}">${actionIcon.getHtml(i18n, 24)}</a>
                </li>
            """
    }

    @Override
    void visitMenuSelect(String paramName, EnumOption[] enumOptions, Map<String, ?> params) {
        String valueSelected = params[paramName]
        EnumOption enumSelected = enumOptions.find { it.key == valueSelected }
        String controller = params['controller'] as String
        String action = params['action'] as String
        visitMenu(enumSelected.value, controller, action, params)
        for (def eo in enumOptions) {
            params.put(paramName, eo.key)
            visitSubMenu(eo.value, controller, action, params)
        }
        visitMenuEnd()
    }

    @Override
    void visitMenuSearch(MethodClosure action, String q, Class<? extends GormEntity>[] aClasses) {
        out << """
            <form class="solrSearch-input" action="${parameter.urlMapped(Utils.getControllerName(action), action.method)}">
                <div class="input-group rounded">
                    <input type="search" id="form1" name="q" value="${q? q.replace('"', "&quot;") : ''}" class="form-control rounded bg-white" placeholder="Search" aria-label="Search"/>
                </div>
            </form>
        """
    }
}
