package taack.solr

import groovy.transform.CompileStatic

/**
 * List index options for a field
 */
@CompileStatic
enum SolrFieldType {
    POINT_INT('_i', 'NearestInts'),    // for ints
    POINT_STRING('_s', 'ExactStr'), // for strings
    POINT_LONG('_l', 'NearestLongs'),   // ... long
    TXT_UNIQ('_t', 'ExactTxt'),
    TXT_GENERAL('_t', 'ExactTxt'),  // lower case
    TXT_GENERAL_MULTI('_txt', 'SimplifiedTxt'),  // lower case
    TXT_NO_ACCENT('_noAccent', 'TxtWoutAccent'),  // lower case
    TXT_GENERAL_EN('_txt_en', 'EnTxt'),  // lower case
    TXT_GENERAL_DE('_txt_de', 'DeTxt'),  // lower case
    TXT_GENERAL_ES('_txt_es', 'EsTxt'),  // lower case
    TXT_GENERAL_FR('_txt_fr', 'FrTxt'),  // lower case
    TXT_GENERAL_IT('_txt_it', 'ItTxt'),  // lower case
    TXT_GENERAL_RU('_txt_ru', 'RuTxt'),  // lower case
    TXT_GENERAL_EN_SPLIT('_txt_en_split', 'SplitEnTxt'),  // lower case
    TXT_PHONE_EN('_phon_en', 'PhoneticEnTxt'),  // lower case
    TXT_WS('_ws', 'NoWSTxt'),      // white space
    TXT_SORT('_t_sort', 'Txt4Facets'),// AFAIU for faceting
    BOOLEAN('_b', 'Bool'),
    POINT_FLOAT('_f', 'Float'),
    DATE('_dt', 'Date'),
    LOCATION('_p', 'Location'),
    POINT_DOUBLE('_d', 'PreciseFloat')

    SolrFieldType(String suffix, String label) {
        this.suffix = suffix
        this.label = label
    }

    final String suffix
    final String label
}