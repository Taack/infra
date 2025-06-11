package taack.solr

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import org.hibernate.proxy.HibernateProxy
import taack.ui.dump.Parameter

/**
 * Specify how to index a domain
 *
 * See {@link taack.domain.TaackSearchService#registerSolrSpecifier(taack.domain.TaackSearchService.IIndexService, taack.solr.SolrSpecifier)}
 * To register a domain search.
 */
@CompileStatic
final class SolrSpecifier {
    final private Class<? extends GormEntity> type
    final private Closure closure

    /**
     * Only constructor
     *
     * @param type The Gorm class
     * @param closure How to index fields for this class (See {@link SolrIndexerFieldSpec}
     */
    SolrSpecifier(Class<? extends GormEntity> type, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = SolrIndexerFieldSpec) Closure closure) {
        if (type.isAssignableFrom(HibernateProxy)) this.type = type.superclass as Class<? extends GormEntity>
        else this.type = type
        this.closure = closure
    }

    Class<? extends GormEntity> getType() {
        type
    }

    void visitSolr(final ISolrIndexerVisitor visitor, GormEntity entity = null) {
        closure.delegate = new SolrIndexerFieldSpec(visitor)
        closure.call(entity)
    }
}
