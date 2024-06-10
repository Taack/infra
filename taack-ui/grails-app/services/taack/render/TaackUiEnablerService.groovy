package taack.render

import grails.compiler.GrailsCompileStatic
import grails.web.api.WebAttributes
import org.codehaus.groovy.runtime.MethodClosure
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator
import taack.ui.dsl.helper.Utils
/**
 * Service enabling to predict if an action is allowed to the end user. This service allows to remove actions
 * links (buttons and links) if the target action is not allowed with those parameters to the end user.
 *
 * <pre>{@code
 *    @PostConstruct
 *  void init() {
 *      TaackUiEnablerService.securityClosure(
 *          { Long id, Map p ->
 *              if (!id && !p) return true
 *              def task = TaskHistory.read(id ?: p['id'] as Long)
 *              def u = springSecurityService.currentUser as User
 *              if (task.userCreated == u) true
 *              else if (task.reporter == u) true
 *              else if (task.assignee?.allUser?.contains(u)) true
 *              else false
 *          },
 *      MkgController.&editTaskAction as MethodClosure,
 *      MkgController.&selectLinkedTask as MethodClosure,
 *      MkgController.&saveLinkedTask as MethodClosure,
 *      MkgController.&addTaskAttachment as MethodClosure)
 *  }
 * }</pre>
 */
@GrailsCompileStatic
class TaackUiEnablerService implements WebAttributes {
    WebInvocationPrivilegeEvaluator webInvocationPrivilegeEvaluator

    private final static Map<String, Closure> securityClosures = [:]

    /**
     * Execute the closure if the actions are a target of a link. If the closure returns true,
     * the action is allowed, if false, it cannot be reached.
     *
     * @param closure must return true or false
     * @param actions list of actions that are secured by the closure
     */
    static void securityClosure(Closure closure, final MethodClosure... actions) {
        actions.each { action ->
            securityClosures.put("/" + Utils.getControllerName(action) + "/" + action.method.toString(), closure)
        }
    }

    /**
     * Check if the action is allowed
     *
     * @param controller
     * @param action
     * @param id
     * @param params
     * @return true if allowed, false if not
     */
    boolean hasAccess(final String controller, final String action, final Long id, final Map params) {
        def authContext = SecurityContextHolder.getContext().getAuthentication()
        if (!authContext?.authenticated) {
            return true
        }

        def path = '/' + controller + '/' + action
        def isAllowed = webInvocationPrivilegeEvaluator.isAllowed(path, authContext)
        if (isAllowed) {
            def c = securityClosures[path]
            if (c) {
                return c.call(id, params)
            }
            return true
        }
        return false
    }

    /**
     * see {@link #hasAccess(String, String, Long, java.util.Map)}
     * @param methodClosure
     * @param id
     * @param params
     * @return
     */
    boolean hasAccess(final MethodClosure methodClosure, Long id = null, Map params = null) {
        hasAccess(Utils.getControllerName(methodClosure), methodClosure.method, id, params)
    }

    /**
     * see {@link #hasAccess(String, String, Long, java.util.Map)}
     * @param methodClosure
     * @param params
     * @return
     */
    boolean hasAccess(final MethodClosure methodClosure, Map params) {
        hasAccess(Utils.getControllerName(methodClosure), methodClosure.method, null, params)
    }

    /**
     * Disallow continuing to execute the action, throwing a AccessDeniedException exception, if user
     * is not allowed to execute the action as predicted.
     */
    void checkAccess() {
        if (!hasAccess(controllerName, actionName, null, params)) {
            throw new AccessDeniedException("Not allowed to execute /$controllerName/$actionName with $params")
        }
    }
}
