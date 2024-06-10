package taack.ui.dump


import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOption
import taack.ui.IEnumOptions
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.helper.Utils
import taack.ui.dsl.menu.IUiMenuVisitor
import taack.ui.dsl.menu.MenuSpec

// TODO: Construct menu using IHTMLElement

@CompileStatic
class RawHtmlMenuDump implements IUiMenuVisitor {
    final ByteArrayOutputStream out
    final String modalId
    final Parameter parameter
    boolean splitted = false

    RawHtmlMenuDump(final ByteArrayOutputStream out, final String modalId, final Parameter parameter) {
        this.out = out
        this.modalId = modalId
        this.parameter = parameter
    }

    @Override
    void visitLabel(String i18n, boolean hasClosure) {
        if (hasClosure) {
            out << """
             <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">${i18n}</a>
                <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
            """
        } else {
            out << """
             <li class="nav-item">
                <a class="nav-link" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">${i18n}</a>
            </li>
            """

        }
    }

    @Override
    void visitLabelEnd() {
        out << "</ul></li>"
    }

    @Override
    void visitMenuStart(MenuSpec.MenuMode menuMode) {
        out << """<ul class="navbar-nav me-auto mb-2 mb-lg-0">"""
    }

    @Override
    void visitMenuStartEnd() {
        splitMenuStop()
        out << """</ul>"""
    }

    private void splitMenuStart() {
        if (!splitted) {
            splitted = true
            out << """</ul><ul class="navbar-nav flex-row ml-md-auto ">"""
        }
    }

    private void splitMenuStop() {
        if (splitted) {
            out << "</ul>"
        }
    }

    @Override
    void visitMenu(String controller, String action, Map<String, ?> params) {
        String i18n = parameter.trField(controller, action)
        visitLabeledSubMenu(i18n, controller, action, params)
    }


    @Override
    void visitSubMenu(String controller, String action, Map<String, ?> params) {
        String i18n = parameter.trField(controller, action)
        visitLabeledSubMenu(i18n, controller, action, params)
    }

    private void visitLabeledSubMenu(String i18n, String controller, String action, Map<String, ?> params) {
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
        i18n ?= parameter.trField(controller, action)
        splitMenuStart()
        if (isModal)
            out << """
                 <li>
                    <a class='nav-link' ajaxAction='${parameter.urlMapped(controller, action, params, true)}'>
                        ${actionIcon.getHtml(i18n, 24)}
                    </a>
                 </li>
            """
        else
            out << """
                <li>
                    <a class="nav-link" href="${parameter.urlMapped(controller, action, params)}">
                        ${actionIcon.getHtml(i18n, 24)}
                    </a>
                </li>
            """
    }

    @Override
    void visitMenuSelect(String paramName, IEnumOptions enumOptions, Map<String, ?> params) {
        String valueSelected = params[paramName]
        IEnumOption enumSelected = enumOptions.getOptions().find { it.key == valueSelected }
        String controller = params['controller'] as String
        String action = params['action'] as String
        visitLabeledSubMenu(enumSelected.value, controller, action, params)
        for (def eo in enumOptions.getOptions()) {
            params.put(paramName, eo.key)
            visitLabeledSubMenu(eo.value, controller, action, params)
        }
    }

    @Override
    void visitMenuSearch(MethodClosure action, String q, Class<? extends GormEntity>[] aClasses) {
        splitMenuStart()
        out << """
            <form class="solrSearch-input py-1" action="${parameter.urlMapped(Utils.getControllerName(action), action.method)}">
                <div class="input-group rounded">
                    <input type="search" id="form1" name="q" value="${q ? q.replace('"', "&quot;") : ''}" class="form-control rounded bg-white" placeholder="Search" aria-label="Search"/>
                </div>
            </form>
        """
    }

    @Override
    void visitMenuOptions(IEnumOptions enumOptions) {
        splitMenuStart()
        String selectedOptionKey = parameter.params[enumOptions.paramKey]

        IEnumOption currentOption = selectedOptionKey ? (enumOptions.options.find { it.key == selectedOptionKey }) : enumOptions.currents?.first() as IEnumOption
        String selectedOptionValue = currentOption ? currentOption.value : selectedOptionKey
        String current = """\
            <a class="nav-link dropdown-toggle" id="navbar${enumOptions.paramKey.capitalize()}" role="button" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                ${currentOption ? parameter.applicationTagLib.img(file: currentOption.asset, width: 20, style: "padding: .5em 0em;") : ''}
                ${selectedOptionValue}
            </a>
        """.stripIndent()

        out << """\
        <li class="nav-item dropdown">
            $current
            <ul class="dropdown-menu" aria-labelledby="navbar${enumOptions.paramKey.capitalize()}">
        """.stripIndent()

        String controller = parameter.params['controller'] as String
        String action = parameter.params['action'] as String

        final IEnumOption[] options = enumOptions.options
        final int im = options.size()

        int i = 0
        for (i; i < im;) {
            IEnumOption option = options[i++]
            parameter.params.put(enumOptions.paramKey, option.key)
            if (option.section) {
                out << """\
                    <li>
                        <a class="dropdown-item" style="color: #887700">
                            ${parameter.applicationTagLib.img(file: option.asset, width: 20, style: "padding: .5em 0em;")}
                            <b>${option.value}</b>
                        </a>
                    </li>
                """.stripIndent()
            } else {
                out << """\
                    <li>
                        <a class='dropdown-item' href='${parameter.urlMapped(controller, action, parameter.params, false)}'>
                            ${parameter.applicationTagLib.img(file: option.asset, width: 20, style: "padding: .5em 0em;")}
                            ${option.value}
                        </a>
                    </li>
                """.stripIndent()
            }
        }
        out << """\
            </ul>
        </li>
        """.stripIndent()
    }
}
