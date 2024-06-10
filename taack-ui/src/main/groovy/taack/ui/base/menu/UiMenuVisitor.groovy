package taack.ui.base.menu

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOption
import taack.ui.IEnumOptions
import taack.ui.base.common.ActionIcon

@CompileStatic
class UiMenuVisitor implements IUiMenuVisitor {

    @Override
    void visitLabel(String i18n, boolean hasClosure) {

    }

    @Override
    void visitLabelEnd() {

    }

    @Override
    void visitMenuStart(MenuSpec.MenuMode menuMode) {

    }

    @Override
    void visitMenuStartEnd() {

    }

    @Override
    void visitMenu(String controller, String action, Map<String, ?> params) {

    }

    @Override
    void visitSubMenu(String controller, String action, Map<String, ?> params) {

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
    void visitMenuSelect(String paramName, IEnumOptions enumOptions, Map<String, ?> params) {

    }

    @Override
    void visitMenuSearch(MethodClosure action, String q, Class<? extends GormEntity>[] aClasses) {

    }

    @Override
    void visitMenuOptions(IEnumOptions enumOptions) {

    }

}
