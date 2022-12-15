package taack.domain

import org.grails.datastore.gorm.GormEntity

interface IEnumTransition2<U extends GormEntity> {
    IEnumTransition2[] transitionsTo(U user)

    List<String> getLockedFields(U user)
}