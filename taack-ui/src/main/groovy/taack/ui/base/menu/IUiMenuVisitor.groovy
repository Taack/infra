package taack.ui.base.menu


import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOption
import taack.ui.IEnumOptions
import taack.ui.base.common.ActionIcon

interface IUiMenuVisitor {

    void visitLabel(String i18n, boolean hasClosure)

    void visitLabelEnd()

    void visitMenuStart(MenuSpec.MenuMode menuMode)

    void visitMenuStartEnd()

    void visitMenu(final String controller, final String action, final Map<String, ? extends Object> params)

    void visitSubMenu(final String controller, final String action, final Map<String, ? extends Object> params)

    void visitSection(final String i18n, final MenuSpec.MenuPosition position)

    void visitSectionEnd()

    void visitSubMenuIcon(String i18n, ActionIcon actionIcon, String controller, String action, Map<String, ? extends Object> params, final boolean isModal)

    void visitMenuSelect(String paramName, IEnumOptions enumOptions, Map<String, ?> params)

    void visitMenuSearch(MethodClosure action, String q, Class<? extends GormEntity>[] aClasses)

    void visitMenuOptions(IEnumOptions enumOptions)}