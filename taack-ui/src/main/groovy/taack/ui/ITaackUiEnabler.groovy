package taack.ui

import org.codehaus.groovy.runtime.MethodClosure

interface ITaackUiEnabler {
    boolean hasAccess(final MethodClosure methodClosure, Map params)
    boolean hasAccess(final MethodClosure methodClosure, Long id, Map params)
    boolean hasAccess(final MethodClosure methodClosure, Long id)
    boolean hasAccess(final MethodClosure methodClosure)
    boolean hasAccess(final String controller, final String action, final Long id, Map params, boolean fromWeb)
}