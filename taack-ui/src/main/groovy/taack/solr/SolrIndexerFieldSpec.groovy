package taack.solr

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo

//TODO: Label should be deduced and optional
/**
 * {@link SolrSpecifier} delegated class
 *
 * Describe how the fields should be indexed, scored and faceted.
 * See {@link taack.domain.TaackSearchService} for a code sample.
 */
@CompileStatic
final class SolrIndexerFieldSpec {
    final private ISolrIndexerVisitor indexerVisitor

    SolrIndexerFieldSpec(ISolrIndexerVisitor indexerVisitor) {
        this.indexerVisitor = indexerVisitor
    }

    /**
     * Add domain class field index
     *
     * @param i18n The label of the field in the search result
     * @param fieldType Kind of index we want for this field, see {@link SolrFieldType}
     * @param boost Boost the rank of the object in results if this field match the search
     * @param faceted Is this field part of the index
     * @param fieldInfo Point to the value using fieldInfo
     */
    void indexField(String i18n, SolrFieldType fieldType, float boost = 1.0f, boolean faceted = false, FieldInfo... fieldInfo) {
        indexerVisitor.index(i18n, fieldType, fieldInfo*.fieldName.join('-'), fieldInfo.last().value, faceted, boost)
    }

    /**
     * Add domain class field index via direct value and specifying fieldPrefix
     *
     * @param i18n
     * @param fieldType
     * @param fieldPrefix Uniq prefix in the Solr Index
     * @param boost
     * @param faceted
     * @param value Value to Index
     */
    void indexField(String i18n, SolrFieldType fieldType, String fieldPrefix, float boost, boolean faceted = false, String value) {
        indexerVisitor.index(i18n, fieldType, fieldPrefix, value, faceted, boost)
    }

    /**
     * Index field with default boost
     *
     * @param i18n
     * @param fieldType
     * @param fieldPrefix Uniq prefix in the Solr Index
     * @param faceted
     * @param value Value to Index
     */
    void indexField(String i18n, SolrFieldType fieldType, String fieldPrefix, boolean faceted = false, String value) {
        indexerVisitor.index(i18n, fieldType, fieldPrefix, value, faceted, 1.0f)
    }

    /**
     * Index field ..
     *
     * @param i18n
     * @param fieldType
     * @param fieldPrefix Uniq prefix in the Solr Index
     * @param faceted
     * @param value Multivalued strings
     */
    void indexField(String i18n, SolrFieldType fieldType, String fieldPrefix, boolean faceted = false, List<String> value) {
        indexerVisitor.index(i18n, fieldType, fieldPrefix, value, faceted, 1.0f)
    }
}
