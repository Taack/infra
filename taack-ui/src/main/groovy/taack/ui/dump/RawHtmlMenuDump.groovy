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
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.menu.BootstrapMenu
import taack.ui.dump.html.theme.ThemeSelector

// TODO: Construct menu using IHTMLElement

@CompileStatic
class RawHtmlMenuDump implements IUiMenuVisitor {
    final ByteArrayOutputStream out
    final String modalId
    final Parameter parameter
    boolean splitted = false

    IHTMLElement topElement
    final BootstrapMenu menu

    RawHtmlMenuDump(final ByteArrayOutputStream out, final String modalId, final Parameter parameter) {
        this.out = out
        this.modalId = modalId
        this.parameter = parameter
        ThemeSelector ts = parameter.uiThemeService.themeSelector
        menu = new BootstrapMenu(ts.themeMode, ts.themeSize)
    }

    private IHTMLElement closeTags(TaackTag tag) {
        IHTMLElement top = topElement
        while (top && top.taackTag != tag) {
            top = top.parent
        }
        (top?.taackTag == tag ? top?.parent : top) ?: menu
    }

    @Override
    void visitLabel(String i18n, boolean hasClosure) {
        topElement = menu.label(this, i18n, hasClosure)
    }

    @Override
    void visitLabelEnd() {
        topElement = closeTags(TaackTag.LABEL)
    }

    @Override
    void visitMenuStart(MenuSpec.MenuMode menuMode) {
        topElement = menu.menuStart(topElement)
    }

    @Override
    void visitMenuStartEnd() {
        topElement = closeTags(TaackTag.MENU)
    }

    private void splitMenuStart() {
        topElement = closeTags(TaackTag.MENU)
        topElement = menu.splitMenuStart(topElement)
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
        menu.menu(topElement, i18n, parameter.urlMapped(controller, action, params))
    }

    @Override
    void visitSection(String i18n, MenuSpec.MenuPosition position) {
        menu.section(topElement, i18n)
    }

    @Override
    void visitSectionEnd() {

    }

    @Override
    void visitSubMenuIcon(String i18n, ActionIcon actionIcon, String controller, String action, Map<String, ?> params, boolean isModal = false) {
        i18n ?= parameter.trField(controller, action)
        splitMenuStart()
        menu.menuIcon(topElement, actionIcon.getHtml(i18n, 24), parameter.urlMapped(controller, action, params, isModal), isModal)
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
        menu.menuSearch(topElement, q.replace('"', "&quot;"), parameter.urlMapped(Utils.getControllerName(action), action.method))
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
                        <a class='dropdown-item' href='${parameter.urlMapped(controller, action, parameter.params as Map, false)}'>
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
