package taack.domain

import grails.web.api.WebAttributes
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import taack.ui.dsl.UiFilterSpecifier
/**
 * Service allowing to automatically filter data in a tableFilter. It is typically
 * used in a table block. It uses params given from the {@link UiFilterSpecifier} to filter data.
 *
 */
@CompileStatic
final class TaackFilterService implements WebAttributes {

    @Autowired
    SessionFactory sessionFactory

    final<T extends GormEntity<T>> TaackFilter.FilterBuilder<T> getBuilder(Class<T> cClass) {
        new TaackFilter.FilterBuilder<T>(cClass, sessionFactory, params.toSorted())
    }

    final<T extends GormEntity<T>> TaackFilter.FilterBuilder<T> getBuilder(T oObject) {
        new TaackFilter.FilterBuilder<T>(oObject, sessionFactory, params.toSorted())
    }

}
