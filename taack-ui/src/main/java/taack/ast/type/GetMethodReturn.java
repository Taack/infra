package taack.ast.type

import groovy.transform.CompileStatic

import java.lang.reflect.Method

/**
 * FieldInfo counterpart for getters. Not used outside show, because we
 * cannot filter or save data from getters.
 * @param <T> The return type of the getter.
 */
@CompileStatic
final class GetMethodReturn<T> {
    final Method method
    final T value

    GetMethodReturn(final Method method, final T value) {
        this.method = method
        this.value = value
    }
}
