package taack.domain


import grails.web.api.WebAttributes
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import taack.ui.base.UiFilterSpecifier
import taack.ui.base.filter.ITaackFilterExtension

/**
 * Service allowing to automatically filter data in a tableFilter. It is typically
 * used in a table block. It uses params given from the {@link UiFilterSpecifier} to filter data.
 *
 */
@CompileStatic
final class TaackFilterService<T extends GormEntity> implements WebAttributes {

    @Autowired
    SessionFactory sessionFactory

    TaackFilter.FilterBuilder getBuilder(Class<T> cClass) {
        new TaackFilter.FilterBuilder(cClass, sessionFactory, params)
    }


}
