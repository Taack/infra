package taack.domain

import groovy.transform.CompileStatic

@CompileStatic
interface IDomainHistory<T> {
    /**
     * Duplicate only direct fields and nextVersion field, not link to other fields and
     * deactivate the object. Unless you know what you are doing
     * @param newVersion
     * @return oldVersion
     */
    T cloneDirectObjectData()

    /**
     *
     * @return list of previous versions ordered
     */
    List<T> getHistory()
}
