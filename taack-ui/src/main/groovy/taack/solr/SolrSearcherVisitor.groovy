package taack.solr

import groovy.transform.CompileStatic
import taack.ui.dump.Parameter

@CompileStatic
class SolrSearcherVisitor implements ISolrIndexerVisitor {
    final List<String> fields = []
    final List<String> facets = []
    final Map<String, String> i18nMap = [:]
    final List<String> boostFields = []
    final Parameter parameter = new Parameter()

    @Override
    void index(SolrFieldType fieldType, String fieldPrefix, Object value, boolean faceted = true, float boost) {
        final String key = fieldPrefix + fieldType.suffix
        i18nMap.put(key, parameter.trField('solr', fieldPrefix) + "[${fieldType.label}]")
        if (fieldType == SolrFieldType.DATE) return
        if (faceted) {
            facets.add key
        }
        fields.add key
        String b = "${key}^${boost}"
        boostFields.add b
    }
}
