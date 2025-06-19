package taack.solr

import groovy.transform.CompileStatic
import org.apache.solr.common.SolrInputDocument
import org.grails.datastore.gorm.GormEntity
import org.hibernate.proxy.HibernateProxy

@CompileStatic
class SolrIndexerVisitor implements ISolrIndexerVisitor {
    final SolrInputDocument document

    SolrIndexerVisitor(SolrInputDocument document, GormEntity entity) {
        this.document = document
        String simpleName = entity.class.simpleName
        if (entity instanceof HibernateProxy) {
            simpleName = entity.class.superclass.simpleName
        }
        document.addField 'id', simpleName + '-' + entity.ident().toString()
        document.addField 'type_s', simpleName
    }

    @Override
    void index(SolrFieldType fieldType, String fieldPrefix, Object value, boolean faceted = true, float boost) {
        document.addField(fieldPrefix + fieldType.suffix, value)
    }
}
