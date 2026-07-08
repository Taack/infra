package taack.ast.type;

import java.lang.reflect.Method;

/**
 * FieldInfo counterpart for getters. Not used outside show, because we
 * cannot filter or save data from getters.
 *
 * @param <T> The return type of the getter.
 */
public final class GetMethodReturn<T> {
    public GetMethodReturn(final Method method, final T value) {
        this.method = method;
        this.value = value;
    }

    public final Method getMethod() {
        return method;
    }

    public final T getValue() {
        return value;
    }

    private final Method method;
    private final T value;
}
