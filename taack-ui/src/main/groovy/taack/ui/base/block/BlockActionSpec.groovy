package taack.ui.base.block

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.render.TaackUiEnablerService
import taack.ui.base.common.ActionIcon
import taack.ui.base.helper.Utils

/**
 * Class allowing to list actions in tables, shows, filterTables and charts header
 */
@CompileStatic
final class BlockActionSpec {
    final IUiBlockVisitor blockVisitor

    TaackUiEnablerService taackUiEnablerService = Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') as TaackUiEnablerService

    BlockActionSpec(final IUiBlockVisitor blockVisitor) {
        this.blockVisitor = blockVisitor
    }

    /**
     * Display an action icon
     *
     * @param i18n Hover text
     * @param icon icon to display
     * @param action target action when clicked
     * @param id ID parameter
     * @param isAjaxRendering has to be true if target action is ajax
     */
    void action(String i18n = null, ActionIcon icon, MethodClosure action, Long id) {
        if (taackUiEnablerService.hasAccess(action, id)) blockVisitor.visitAction(i18n, icon, Utils.getControllerName(action), action.method, id, null, true)
    }

    /**
     *
     * @param i18n
     * @param icon
     * @param action
     * @param params
     * @param isAjaxRendering
     */
    void action(String i18n = null, ActionIcon icon, MethodClosure action, Map params) {
        if (taackUiEnablerService.hasAccess(action, params)) blockVisitor.visitAction(i18n, icon, Utils.getControllerName(action), action.method, null, params, true)
    }

    /**
     *
     * @param i18n
     * @param icon
     * @param action
     * @param isAjaxRendering Default to False
     */
    void action(String i18n = null, ActionIcon icon, MethodClosure action) {
        if (taackUiEnablerService.hasAccess(action)) blockVisitor.visitAction(i18n, icon, Utils.getControllerName(action), action.method, null, null, true)
    }

}
