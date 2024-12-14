package taack.ui.dsl.show

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style

@CompileStatic
interface IUiShowVisitor {

    void visitShow()

    void visitShowEnd()

    void visitSection(final String i18n)

    void visitSectionEnd()

    void visitShowFieldUnLabeled(final Style style, final FieldInfo... fields)

    void visitShowFieldLabeled(final Style style, final FieldInfo... fields)

    void visitShowFieldUnLabeled(final Style style, final GetMethodReturn methodReturn)

    void visitShowFieldLabeled(final Style style, final GetMethodReturn methodReturn)

    void visitShowField(final String i18n, final FieldInfo field, final Style style)

    void visitShowField(final String i18n, final String field, final Style style)

    void visitShowField(final String html)

    void visitShowAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, Object> additionalParams, boolean isAjax)

    void visitShowAction(String i18n, String linkText, String controller, String action, Long id, Map additionalParams, boolean isAjax)

    void visitShowInlineHtml(String html, String additionalCSSClass)
}