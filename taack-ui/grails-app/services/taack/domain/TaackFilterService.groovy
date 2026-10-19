package taack.domain

import grails.web.api.WebAttributes
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import taack.ui.dsl.UiFilterSpecifier
/**
 * Service providing query filter builders for GORM entities, driven by HTTP request parameters and
 * {@link UiFilterSpecifier}.
 */
@CompileStatic
final class TaackFilterService implements WebAttributes {

    @Autowired
    SessionFactory sessionFactory

    /**
     * Creates a {@link TaackFilter.FilterBuilder} for the specified GORM entity class.
     *
     * @param cClass the target entity class
     * @return a builder initialized with the session factory and current request parameters
     */
    final<T extends GormEntity> TaackFilter.FilterBuilder<T> getBuilder(Class<T> cClass) {
        new TaackFilter.FilterBuilder<T>(cClass, sessionFactory, params.toSorted())
    }

    /**
     * Creates a {@link TaackFilter.FilterBuilder} using an existing GORM entity instance as prototype.
     *
     * @param oObject the prototype entity instance
     * @return a builder initialized with the session factory and current request parameters
     */
    final<T extends GormEntity> TaackFilter.FilterBuilder<T> getBuilder(T oObject) {
        new TaackFilter.FilterBuilder<T>(oObject, sessionFactory, params.toSorted())
    }

}
