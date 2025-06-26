package taack.ui.dump

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOptions
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.menu.IUiMenuVisitor
import taack.ui.dsl.menu.MenuSpec
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.layout.HTMLEmpty
import taack.ui.dump.html.menu.DropdownMenu

@CompileStatic
class RawHtmlDropdownMenuDump implements IUiMenuVisitor {

    private final BlockLog blockLog
    final Parameter parameter
    DropdownMenu menu
    String futurCurrentAjaxBlockId

    RawHtmlDropdownMenuDump(final Parameter parameter) {
        this.parameter = parameter
        blockLog = new BlockLog(parameter.uiThemeService.themeSelector)
        blockLog.topElement = new HTMLEmpty()
        blockLog.topElement.setTaackTag(TaackTag.MENU_BLOCK)
    }

    void enterBlock(String toPrint) {
        blockLog.enterBlock(RawHtmlDropdownMenuDump.simpleName + '::' + toPrint)
    }

    void exitBlock(String toPrint) {
        blockLog.exitBlock(RawHtmlDropdownMenuDump.simpleName + '::' + toPrint)
    }

    @Override
    void visitMenuLabel(String i18n, boolean hasClosure) {
    }

    @Override
    void visitMenuLabelEnd() {
    }

    @Override
    void visitMenuStart(MenuSpec.MenuMode menuMode, String ajaxBlockId) {
        enterBlock('visitMenuStart futurCurrentAjaxBlockId: ' + ajaxBlockId)
        futurCurrentAjaxBlockId = ajaxBlockId
        blockLog.topElement.setTaackTag(TaackTag.MENU_CONTEXTUAL)
        menu = new DropdownMenu(parameter.target == Parameter.RenderingTarget.MAIL, blockLog)
        blockLog.topElement = menu.menuStart(blockLog.topElement)
    }

    @Override
    void visitMenuStartEnd() {
        exitBlock('visitMenuStartEnd')
        futurCurrentAjaxBlockId = null
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.MENU_CONTEXTUAL)
    }

    @Override
    void visitMenu(String controller, String action, Map<String, ?> params) {
        blockLog.stayBlock('visitMenu')
        visitLabeledSubMenu(null, controller, action, params)
    }

    @Override
    void visitSubMenu(String controller, String action, Map<String, ?> params) {

    }

    @Override
    void visitLabeledSubMenu(String i18n, String controller, String action, Map<String, ?> params) {
        i18n ?= parameter.trField(controller, action, params?.containsKey('id'))

        blockLog.stayBlock('visitLabeledSubContextualMenu ' + i18n)
        Map cp = parameter.params
        if (params) {
            if (cp.containsKey('lang') && !params.containsKey('lang')) cp.remove('lang')
            if (cp.containsKey('action')) cp.remove('action')
            if (cp.containsKey('controller')) cp.remove('controller')
            if (futurCurrentAjaxBlockId && parameter.controllerName == controller && parameter.actionName == action) {
                params.put('ajaxBlockId', futurCurrentAjaxBlockId)
                params.put('refresh', 'true')
                if (parameter.tabId && parameter.tabIndex) {
                    params.put('tabId', parameter.tabId)
                    params.put('tabIndex', parameter.tabIndex)
                }
            }
        }
        blockLog.topElement = menu.menu(blockLog.topElement, i18n, futurCurrentAjaxBlockId != null && !futurCurrentAjaxBlockId.empty, futurCurrentAjaxBlockId, parameter.urlMapped(controller, action, params), controller == parameter.controllerName && action == parameter.actionName && (!params || params.equals(cp)))
    }

    @Override
    void visitMenuSection(String i18n, MenuSpec.MenuPosition position) {

    }

    @Override
    void visitMenuSectionEnd() {

    }

    @Override
    void visitSubMenuIcon(String i18n, ActionIcon actionIcon, String controller, String action, Map<String, ?> params, boolean isModal) {
        i18n ?= parameter.trField(controller, action, params?.containsKey('id'))
        blockLog.stayBlock('visitSubMenuIcon ' + i18n)

        if (parameter.target != Parameter.RenderingTarget.MAIL)
            menu.menuIcon(blockLog.topElement, actionIcon.getHtml(i18n, 24), parameter.urlMapped(controller, action, params, isModal), isModal)
        else
            menu.menu(blockLog.topElement, i18n, false, null, parameter.urlMapped(controller, action, params, isModal))
    }

    @Override
    void visitMenuSelect(String paramName, IEnumOptions enumOptions, Map<String, ?> params) {

    }

    @Override
    void visitMenuSearch(MethodClosure action, String q, Class<? extends GormEntity>[] aClasses) {

    }

    @Override
    void visitMenuOptions(IEnumOptions enumOptions) {

    }

}
