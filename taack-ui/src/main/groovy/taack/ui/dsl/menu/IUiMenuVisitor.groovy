package taack.ui.dsl.menu


import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOptions
import taack.ui.dsl.common.ActionIcon
import taack.ui.dump.html.element.IHTMLElement

interface IUiMenuVisitor extends IHTMLElement {

    void visitMenuLabel(String i18n, boolean hasClosure)

    void visitMenuLabelEnd()

    void visitMenuStart(MenuSpec.MenuMode menuMode, String ajaxBlockId)

    void visitMenuStartEnd()

    void visitMenu(final String controller, final String action, final Map<String, ? extends Object> params)

    void visitSubMenu(final String controller, final String action, final Map<String, ? extends Object> params)

    void visitMenuSection(final String i18n, final MenuSpec.MenuPosition position)

    void visitMenuSectionEnd()

    void visitSubMenuIcon(String i18n, ActionIcon actionIcon, String controller, String action, Map<String, ? extends Object> params, final boolean isModal)

    void visitMenuSelect(String paramName, IEnumOptions enumOptions, Map<String, ?> params)

    void visitMenuSearch(MethodClosure action, String q, Class<? extends GormEntity>[] aClasses)

    void visitMenuOptions(IEnumOptions enumOptions)}