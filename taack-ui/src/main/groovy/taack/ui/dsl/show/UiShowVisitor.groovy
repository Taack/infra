package taack.ui.dsl.show

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style

@CompileStatic
class UiShowVisitor implements IUiShowVisitor {

    @Override
    void visitShow(Object currentObject, String controller, String action) {

    }

    @Override
    void visitShowEnd() {

    }

    @Override
    void visitSection(String i18n) {

    }

    @Override
    void visitSectionEnd() {

    }

    @Override
    void visitShowFieldUnLabeled(Style style, FieldInfo... fields) {

    }

    @Override
    void visitShowFieldLabeled(Style style, FieldInfo... fields) {

    }

    @Override
    void visitShowFieldUnLabeled(Style style, GetMethodReturn methodReturn) {

    }

    @Override
    void visitShowFieldLabeled(Style style, GetMethodReturn methodReturn) {

    }

    @Override
    void visitShowField(String i18n, FieldInfo field, Style style) {

    }

    @Override
    void visitShowField(String i18n, String field, Style style) {

    }

    @Override
    void visitShowField(String html) {

    }

    @Override
    void visitShowAction(String i18n, String controller, String action, Long id, Map additionalParams, boolean isAjax = true) {

    }

    @Override
    void visitShowInputField(String i18n, FieldInfo fieldInfo, boolean isAjax = false) {

    }

    @Override
    void visitFieldAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, Object> additionalParams, boolean isAjax) {

    }

    @Override
    void visitShowInlineHtml(String html, String additionalCSSClass) {

    }
}
