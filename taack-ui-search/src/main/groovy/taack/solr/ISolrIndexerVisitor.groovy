package taack.solr

interface ISolrIndexerVisitor {
    void index(SolrFieldType fieldType, String fieldPrefix, Object value, boolean faceted, float boost)
}
