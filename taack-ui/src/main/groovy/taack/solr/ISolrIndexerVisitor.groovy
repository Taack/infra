package taack.solr

interface ISolrIndexerVisitor {
    void index(String i18n, SolrFieldType fieldType, String fieldPrefix, Object value, boolean faceted, float boost)
}
