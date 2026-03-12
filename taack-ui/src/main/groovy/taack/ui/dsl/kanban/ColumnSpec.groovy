package taack.ui.dsl.kanban

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.render.TaackUiEnablerService
import taack.ui.dsl.common.Style

/**
 * {@link taack.ui.dsl.kanban.KanbanSpec(groovy.lang.Closure)} delegated class.
 *
 * <p>A column can contains many cards
 */
@CompileStatic
final class ColumnSpec {
    final IUiKanbanVisitor kanbanVisitor
    TaackUiEnablerService taackUiEnablerService = Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') as TaackUiEnablerService

    ColumnSpec(IUiKanbanVisitor kanbanVisitor) {
        this.kanbanVisitor = kanbanVisitor
    }

    /**
     * Display kanban column header
     *
     * @param closure
     */
    void header(final String i18n, final Style style = null) {
        kanbanVisitor.visitColumnHeader(i18n, style)
    }

    void card(GormEntity gorm, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = CardFieldSpec) Closure closure) {
        kanbanVisitor.visitCard(gorm, null, null)
        closure.delegate = new CardFieldSpec(kanbanVisitor)
        closure.call()
        kanbanVisitor.visitCardEnd()
    }

    void card(GormEntity gorm, MethodClosure action, Map<String, ? extends Object> params = null, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = CardFieldSpec) Closure closure) {
        kanbanVisitor.visitCard(gorm, taackUiEnablerService.hasAccess(action, params) ? action : null, params)
        closure.delegate = new CardFieldSpec(kanbanVisitor)
        closure.call()
        kanbanVisitor.visitCardEnd()
    }

    void card(GormEntity gorm, MethodClosure action, Long id, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = CardFieldSpec) Closure closure) {
        kanbanVisitor.visitCard(gorm, taackUiEnablerService.hasAccess(action, id) ? action : null, id ? [id: id] : null)
        closure.delegate = new CardFieldSpec(kanbanVisitor)
        closure.call()
        kanbanVisitor.visitCardEnd()
    }

    void custom(final String html, final Style style = null) {
        kanbanVisitor.visitCustom(html, style)
    }
}
