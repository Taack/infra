package taack.ui.dump

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOption
import taack.ui.IEnumOptions
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
    void visitMenu(String controller, String action, Map<String, ?> params) {
        String i18n = parameter.trField(controller, action)
        visitMenu(i18n, controller, action, params)
    }

    @Override
    void visitMenu(String i18n, String controller, String action, Map<String, ?> params) {
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
    void visitSubMenu(String controller, String action, Map<String, ?> params) {
        String i18n = parameter.trField(controller, action)
        visitSubMenu(i18n, controller, action, params)
    }

    @Override
    void visitSubMenu(String i18n, String controller, String action, Map<String, ?> params) {
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
    void visitMenuSelect(String paramName, IEnumOptions enumOptions, Map<String, ?> params) {
        String valueSelected = params[paramName]
        IEnumOption enumSelected = enumOptions.getOptions().find { it.key == valueSelected }
        String controller = params['controller'] as String
        String action = params['action'] as String
        visitMenu(enumSelected.value, controller, action, params)
        for (def eo in enumOptions.getOptions()) {
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
                    <input type="search" id="form1" name="q" value="${q ? q.replace('"', "&quot;") : ''}" class="form-control rounded bg-white" placeholder="Search" aria-label="Search"/>
                </div>
            </form>
        """
    }

    @Override
    void visitMenuOptions(IEnumOptions enumOptions, IEnumOption selectedOption, IEnumOption defaultOption) {

        if (selectedOption) {
            out << """\
            <li class="nav-item" taackOptionKey="${selectedOption.key}" taackOptionParamKey="${enumOptions.paramKey}">
                <a class="nav-link dropdown-toggle show" id="navbarLang" role="button" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="true" >
                    <img src="${selectedOption.asset}" width="20">
                    ${selectedOption.value}
                </a>
            </li>
            """.stripIndent()
        }

        out << """\
        <li class="nav-item dropdown">
            <ul class="dropdown-menu" aria-labelledby="navbarLang">
        """.stripIndent()

        String controller = parameter.params['controller'] as String
        String action = parameter.params['action'] as String

        for (IEnumOption option in enumOptions.options) {
            parameter.params.put(enumOptions.paramKey, option.key)
            if (option.section) {
                out << """\
                    <li class="nav-item">
                        <a class='taackAjaxMenuLink pure-menu-link' style="color: #887700">
                            <asset:image src="${option.asset}" width="20"/>
                            <b>${option.value}</b>
                        </g:link>
                    </li>
                """.stripIndent()

            } else {
                out << """\
                    <li class="nav-item">
                        <a class='taackAjaxMenuLink pure-menu-link' ajaxAction='${parameter.urlMapped(controller, action, parameter.params, true)}' }>
                            <asset:image src="${option.asset}" width="20"/>
                            ${option.value}
                        </g:link>
                    </li>
                """.stripIndent()
            }
        }
        out << """\\     
            </ul>
        </li>
        """.stripIndent()
    }
}
