package taack.ui.dsl.kanban


import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ui.ITaackUiEnabler
import taack.ui.dsl.common.Style
import taack.ui.dump.Parameter
/**
 * {@link taack.ui.dsl.kanban.KanbanSpec(groovy.lang.Closure)} delegated class.
 *
 * <p>A column can contains many cards
 */
@CompileStatic
final class ColumnSpec {
    final IUiKanbanVisitor kanbanVisitor
    ITaackUiEnabler taackUiEnablerService = Parameter.taackUiEnabler

    ColumnSpec(IUiKanbanVisitor kanbanVisitor) {
        this.kanbanVisitor = kanbanVisitor
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
