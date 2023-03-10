package taack.ui.base.menu

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ui.EnumOption
import taack.ui.base.common.ActionIcon

@CompileStatic
class UiMenuVisitor implements IUiMenuVisitor {
    @Override
    void visitMenuStart(MenuSpec.MenuMode menuMode) {

    }

    @Override
    void visitMenuStartEnd() {

    }

    @Override
    void visitMenu(String i18n, String controller, String action, Map<String, ? extends Object> params) {

    }

    @Override
    void visitMenuEnd() {

    }

    @Override
    void visitSubMenu(String i18n, String controller, String action, Map<String, ? extends Object> params) {

    }

    @Override
    void visitSection(String i18n, MenuSpec.MenuPosition position) {

    }

    @Override
    void visitSectionEnd() {

    }

    @Override
    void visitSubMenuIcon(String i18n, ActionIcon actionIcon, String controller, String action, Map<String, ?> params, boolean isModal) {

    }

    @Override
    void visitMenuSelect(String paramName = null, EnumOption[] enumOptions, Map<String, ?> params = null) {

    }

    @Override
    void visitMenuSearch(MethodClosure action, String q, Class<? extends GormEntity>[] aClasses) {

    }
}
