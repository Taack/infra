package taack.ui.base.show

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style

@CompileStatic
interface IUiShowVisitor {

    void visitShow(Object currentObject, String controller, String action)

    void visitShowEnd()

    void visitSection(final String i18n)

    void visitSectionEnd()

    void visitShowFieldUnLabeled(final Style style, final FieldInfo... fields)

    void visitShowFieldLabeled(final Style style, final FieldInfo... fields)

    void visitShowField(final String i18n, final FieldInfo field, final Style style)

    void visitShowField(final String i18n, final String field, final Style style)

    void visitShowField(final String html)

    void visitShowAction(String i18n, String controller, String action, Long id, Map additionalParams, boolean isAjax)

    void visitShowInputField(String i18n, FieldInfo fieldInfo, boolean isAjax)

    void visitFieldAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, Object> additionalParams, boolean isAjax)

    void visitShowInlineHtml(String html, String additionalCSSClass)
}