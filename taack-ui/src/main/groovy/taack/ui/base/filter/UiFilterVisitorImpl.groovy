package taack.ui.base.filter

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ui.IEnumOption
import taack.ui.base.filter.expression.FilterExpression

@CompileStatic
class UiFilterVisitorImpl implements IUiFilterVisitor {
    @Override
    void visitFilterFieldReverse(String i18n, Class reverseClass, FieldInfo reverseField, FieldInfo... fields) {

    }

    @Override
    void visitFilterFieldExpressionBool(FilterExpression... filterExpression) {

    }

    @Override
    void visitFilterFieldExpressionBool(String i18n, Boolean defaultValue, FilterExpression[] filterExpressions) {

    }

    @Override
    void visitFilterAction(String i18n, MethodClosure action) {

    }

    @Override
    void visitFilterExtension(String i18n, FieldInfo... field) {

    }

    @Override
    void visitFilter(Class aClass, Map<String, ? extends Object> additionalParams) {

    }

    @Override
    void visitHiddenId(Long id) {

    }

    @Override
    void visitFilterEnd() {

    }

    @Override
    void visitSection(final String i18n) {

    }

    @Override
    void visitSectionEnd() {

    }

    @Override
    void visitFilterField(String i18n, IEnumOption[] enumOptions, FieldInfo... fields) {

    }
}
