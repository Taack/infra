package taack.ui.base.block

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.base.TaackUiEnablerService
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
    void action(String i18n, ActionIcon icon, MethodClosure action, Long id, boolean isAjaxRendering = false) {
        if (taackUiEnablerService.hasAccess(action, id)) blockVisitor.visitAction(i18n, icon, Utils.getControllerName(action), action.method, id, null, isAjaxRendering)
    }

    /**
     * See {@link #action(java.lang.String, taack.ui.base.common.ActionIcon, org.codehaus.groovy.runtime.MethodClosure, java.lang.Long, boolean)}
     *
     * @param i18n
     * @param icon
     * @param action
     * @param params
     * @param isAjaxRendering
     */
    void action(String i18n, ActionIcon icon, MethodClosure action, Map params, boolean isAjaxRendering = false) {
        if (taackUiEnablerService.hasAccess(action, params)) blockVisitor.visitAction(i18n, icon, Utils.getControllerName(action), action.method, null, params, isAjaxRendering)
    }

    /**
     * See {@link #action(java.lang.String, taack.ui.base.common.ActionIcon, org.codehaus.groovy.runtime.MethodClosure, java.lang.Long, boolean)}
     *
     * @param i18n
     * @param icon
     * @param action
     * @param isAjaxRendering Default to False
     */
    void action(String i18n, ActionIcon icon, MethodClosure action, boolean isAjaxRendering = false) {
        if (taackUiEnablerService.hasAccess(action)) blockVisitor.visitAction(i18n, icon, Utils.getControllerName(action), action.method, null, null, isAjaxRendering)
    }

    /**
     * Display a button that opens an url outside of the intranet in a new tab
     *
     * @param i18n Translation that will be displayed when hovering the button
     * @param icon Button icon
     * @param baseUrl The base url of the website which will be opened
     * @param params A map of the params that will be passed as GET, directly after baseUrl (See Utils.paramsString for parsing of the map into GET params)
     */
    void outsideAction(String i18n, ActionIcon icon, String baseUrl, Map params) {
        blockVisitor.visitOutsideAction(i18n, icon, baseUrl, params)
    }
}
