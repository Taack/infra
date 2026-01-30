package taack.ui.dsl.kanban

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style

import java.text.NumberFormat

@CompileStatic
interface IUiKanbanVisitor {
    void visitKanban()

    void visitKanbanEnd()

    void visitKanbanWithoutFilter()

    void visitColumn(MethodClosure action, Map<String, ? extends Object> params)

    void visitColumnEnd()

    void visitColumnHeader(String i18n, Style style)

    void visitCard(FieldInfo cardId)

    void visitCardEnd()

    void visitCardFieldRaw(String value, Style style)

    void visitCardField(FieldInfo fieldInfo, Long id, String format, final Style style)

    void visitCardField(GetMethodReturn fieldInfo, String format, final Style style)

    void visitCardField(String value, final Style style)

    void visitCardField(Number value, NumberFormat locale, final Style style)

    void visitCardAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, ?> params, Boolean isAjax)

    void visitCardAction(String linkText, String controller, String action, Long id, Map<String, ?> params, Boolean isAjax)

    void visitCardAction(String i18n, ActionIcon actionIcon, String key, String label)
}