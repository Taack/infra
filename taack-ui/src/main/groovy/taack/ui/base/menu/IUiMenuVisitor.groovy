package taack.ui.base.menu

import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ui.EnumOption
import taack.ui.base.common.ActionIcon

interface IUiMenuVisitor {

    void visitMenuStart(MenuSpec.MenuMode menuMode)

    void visitMenuStartEnd()

    void visitMenu(final String i18n, final String controller, final String action, final Map<String, ? extends Object> params)

    void visitMenuEnd()

    void visitSubMenu(final String i18n, final String controller, final String action, final Map<String, ? extends Object> params)

    void visitSection(final String i18n, final MenuSpec.MenuPosition position)

    void visitSectionEnd()

    void visitSubMenuIcon(String i18n, ActionIcon actionIcon, String controller, String action, Map<String, ? extends Object> params, final boolean isModal)

    void visitMenuSelect(String paramName, EnumOption[] enumOptions, Map<String, ?> params)

    void visitMenuSearch(MethodClosure action, String q, Class<? extends GormEntity>[] aClasses)
}