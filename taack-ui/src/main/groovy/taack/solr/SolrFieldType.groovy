package taack.solr

import groovy.transform.CompileStatic

/**
 * List index options for a field
 */
@CompileStatic
enum SolrFieldType {
    POINT_INT("_i"),    // for ints
    POINT_STRING("_s"), // for strings
    POINT_LONG("_l"),   // ... long
    TXT_UNIQ("_t"),
    TXT_GENERAL("_t"),  // lower case
    TXT_GENERAL_MULTI("_txt"),  // lower case
    TXT_NO_ACCENT("_noAccent"),  // lower case
    TXT_GENERAL_EN("_txt_en"),  // lower case
    TXT_GENERAL_DE("_txt_de"),  // lower case
    TXT_GENERAL_ES("_txt_es"),  // lower case
    TXT_GENERAL_FR("_txt_fr"),  // lower case
    TXT_GENERAL_IT("_txt_it"),  // lower case
    TXT_GENERAL_RU("_txt_ru"),  // lower case
    TXT_GENERAL_EN_SPLIT("_txt_en_split"),  // lower case
    TXT_PHONE_EN("_phon_en"),  // lower case
    TXT_WS("_ws"),      // white space
    TXT_SORT("_t_sort"),// AFAIU for faceting
    BOOLEAN("_b"),
    POINT_FLOAT("_f"),
    DATE("_dt"),
    LOCATION("_p"),
    POINT_DOUBLE("_d")

    SolrFieldType(String suffix) {
        this.suffix = suffix
    }

    final String suffix
}