package taack.ui.dump

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOptions
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.menu.IUiMenuVisitor
import taack.ui.dsl.menu.MenuSpec
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.layout.HTMLEmpty
import taack.ui.dump.html.menu.BootstrapMenu

@CompileStatic
class RawHtmlDropdownMenuDump implements IUiMenuVisitor {

    private final BlockLog blockLog
    final Parameter parameter
    BootstrapMenu menu

    RawHtmlDropdownMenuDump(final Parameter parameter) {
        this.parameter = parameter
        blockLog = new BlockLog(parameter.uiThemeService.themeSelector)
        blockLog.topElement = new HTMLEmpty()
        blockLog.topElement.setTaackTag(TaackTag.MENU_BLOCK)
    }
    @Override
    void visitMenuLabel(String i18n, boolean hasClosure) {
    }

    @Override
    void visitMenuLabelEnd() {
    }

    @Override
    void visitMenuStart(MenuSpec.MenuMode menuMode, String ajaxBlockId) {

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
    void visitLabeledSubMenu(String i18n, String controller, String action, Map<String, ?> params) {

    }

    @Override
    void visitMenuSection(String i18n, MenuSpec.MenuPosition position) {

    }

    @Override
    void visitMenuSectionEnd() {

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
