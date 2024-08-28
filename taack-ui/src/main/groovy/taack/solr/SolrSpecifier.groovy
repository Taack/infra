package taack.solr

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import org.hibernate.proxy.HibernateProxy
import taack.ui.dump.Parameter

/**
 * Specify how to index a domain, how to label it in the search.
 *
 * See {@link taack.domain.TaackSearchService#registerSolrSpecifier(taack.domain.TaackSearchService.IIndexService, taack.solr.SolrSpecifier)}
 * To register a domain search.
 */
@CompileStatic
final class SolrSpecifier {
    final private Class<? extends GormEntity> type
    final private Closure closure
    final private MethodClosure show
    final private MethodClosure label

    /**
     * Only constructor
     *
     * @param type The Gorm class
     * @param show The action to show when clicking on the object in the search. The access control should be managed from there
     * @param label The method which will label the search entry for this object
     * @param closure How to index fields for this class (See {@link SolrIndexerFieldSpec}
     */
    SolrSpecifier(Class<? extends GormEntity> type, MethodClosure show, MethodClosure label, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = SolrIndexerFieldSpec) Closure closure) {
        if (type.isAssignableFrom(HibernateProxy)) this.type = type.superclass as Class<? extends GormEntity>
        else this.type = type
        this.closure = closure
        this.show = show
        this.label = label
    }

    Class<? extends GormEntity> getType() {
        type
    }

    /**
     *
     * @return Method used to show the object when clicking on the label of the object in the search result list
     */
    MethodClosure getShow() {
        show
    }

    /**
     *
     * @return Method called to construct the object label in the search result list
     */
    MethodClosure getLabel() {
        label
    }

    void visitSolr(final ISolrIndexerVisitor visitor, GormEntity entity = null) {
        closure.delegate = new SolrIndexerFieldSpec(visitor)
        closure.call(entity)
    }
}
