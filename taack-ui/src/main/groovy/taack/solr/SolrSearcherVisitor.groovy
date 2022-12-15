package taack.solr

import groovy.transform.CompileStatic

@CompileStatic
class SolrSearcherVisitor implements ISolrIndexerVisitor {
    final List<String> fields = []
    final List<String> facets = []
    final Map<String, String> i18nMap = [:]
    final List<String> boostFields = []

    @Override
    void index(String i18n, SolrFieldType fieldType, String fieldPrefix, Object value, boolean faceted = true, float boost) {
        final String key = fieldPrefix + fieldType.suffix
        i18nMap.put(key, i18n)
        if (fieldType == SolrFieldType.DATE) return
        if (faceted) {
            facets.add key
        }
        fields.add key
        String b = "${key}^${boost}"
        boostFields.add b
    }
}
