package taack.jdbc

import groovy.transform.CompileStatic

@CompileStatic
class TaackJdbcError extends Throwable {
    final String errorStep
    final String msg

    TaackJdbcError(String errorStep, String msg) {
        this.errorStep = errorStep
        this.msg = msg
    }
}
