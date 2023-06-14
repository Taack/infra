package taack.ui.base.filter

import taack.ui.EnumOption

interface ITaackFilterExtension {
    /**
     * Construct a string to be added in the where clause to filter data.
     *
     * @param type      class this extension applies to
     * @param aliasKey  represent the name of the current field
     * @param query     the query string
     * @return
     */
    String queryToWherePart(Class type, String aliasKey, String query)
    List<EnumOption> enumChoices()
}
