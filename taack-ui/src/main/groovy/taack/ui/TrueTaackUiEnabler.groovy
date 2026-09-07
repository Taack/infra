package taack.ui

import org.codehaus.groovy.runtime.MethodClosure

class TrueTaackUiEnabler implements ITaackUiEnabler {
    @Override
    boolean hasAccess(MethodClosure methodClosure, Map params) {
        return true
    }

    @Override
    boolean hasAccess(MethodClosure methodClosure, Long id, Map params) {
        return true
    }

    @Override
    boolean hasAccess(MethodClosure methodClosure, Long id) {
        return true
    }

    @Override
    boolean hasAccess(MethodClosure methodClosure) {
        return true
    }

    @Override
    boolean hasAccess(String controller, String action, Long id, Map params, boolean fromWeb) {
        return true
    }
}
