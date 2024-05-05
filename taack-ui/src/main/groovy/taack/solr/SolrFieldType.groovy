package taack.solr

import groovy.transform.CompileStatic

/**
 * List index options for a field
 */
@CompileStatic
enum SolrFieldType {
    POINT_INT('_i', 'Ints'),    // for ints
    POINT_STRING('_s', 'Str'), // for strings
    POINT_LONG('_l', 'Long'),   // ... long
    TXT_UNIQ('_t', 'TxtKey'),
    TXT_GENERAL('_t', 'GenTxt'),  // lower case
    TXT_GENERAL_MULTI('_txt', 'GenTxtMulti'),  // lower case
    TXT_NO_ACCENT('_noAccent', 'NoAccentTxt'),  // lower case
    TXT_GENERAL_EN('_txt_en', 'GenEnTxt'),  // lower case
    TXT_GENERAL_DE('_txt_de', 'GenDeTxt'),  // lower case
    TXT_GENERAL_ES('_txt_es', 'GenEsTxt'),  // lower case
    TXT_GENERAL_FR('_txt_fr', 'GenFrTxt'),  // lower case
    TXT_GENERAL_IT('_txt_it', 'GenItTxt'),  // lower case
    TXT_GENERAL_RU('_txt_ru', 'GenRuTxt'),  // lower case
    TXT_GENERAL_EN_SPLIT('_txt_en_split', 'GenEnTxt'),  // lower case
    TXT_PHONE_EN('_phon_en', 'PhonEnTxt'),  // lower case
    TXT_WS('_ws', 'SpaceTxt'),      // white space
    TXT_SORT('_t_sort', 'FacetTxt'),// AFAIU for faceting
    BOOLEAN('_b', 'Bool'),
    POINT_FLOAT('_f', 'Float'),
    DATE('_dt', 'EnTxt'),
    LOCATION('_p', 'Loc'),
    POINT_DOUBLE('_d', 'Double')

    SolrFieldType(String suffix, String label) {
        this.suffix = suffix
        this.label = label
    }

    final String suffix
    final String label
}