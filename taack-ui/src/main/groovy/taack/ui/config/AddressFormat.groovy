package taack.ui.config

import groovy.transform.CompileStatic

@CompileStatic
enum DeliveryType {
    CARE_PO('Care of post office', 'CARE PO'),
    CMB('Community mail bag', 'CMB'),
    GPO_BOX('General Post Box (in capital cities)', 'GPO BOX'),
    MS('Mail service', 'MS'),
    RSD('Roadside delivery', 'RSD'),
    RMS('Roadside mail service', 'RMS'),
    CMA('Community mail agent', 'CMA'),
    CPA('Community postal agent\t', 'CPA'),
    LOCKED_BAG('Locked bag', 'LOCKED BAG'),
    RMB('Roadside mail box/bag', 'RMB'),
    PRIVATE_BAG('Private bag', 'PRIVATE BAG')

    DeliveryType(String label, String abbrev) {
        this.label = label
        this.abbrev = abbrev
    }

    String label
    String abbrev
}


@CompileStatic
enum AddressAreaType {
    RURAL, URBAN, BUSINESS, RESIDENTIAL, PO_BOX, POST_RESTANT
}

// TODO: Bad idea, kept for Wikipedia links
// https://en.wikipedia.org/wiki/House_numbering
@CompileStatic
enum AddressFormatSettings {
    // https://en.wikipedia.org/wiki/Postal_codes_in_Argentina
    ARGENTINA(true, false, true, false, false, false, false),
    // https://en.wikipedia.org/wiki/Postcodes_in_Australia
    // http://auspost.com.au/personal/addressing-guidelines.html
    AUSTRALIA(false, false, false, true, true, true, false),
    // https://en.wikipedia.org/wiki/Postal_codes_in_Austria
    AUSTRIA(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Postal_codes_in_Bangladesh
    BANGLADESH(true, false, false, false, false, false, false),
    BELARUS(true, false, false, false, false, false, false),
    // https://web.archive.org/web/20130420085220/http://www.bpost.be/site/fr/residential/letters-cards/send/best_practices.html
    // https://en.wikipedia.org/wiki/Bpost
    // https://www.upu.int/en/Postal-Solutions/Programmes-Services/Addressing-Solutions#scroll-nav__5
    BELGIUM(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Federative_units_of_Brazil
    BRAZIL(true, false, false, false, false, false, false),
    BULGARIA(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Postal_codes_in_Canada
    // https://en.wikipedia.org/wiki/Canada_Post
    // https://en.wikipedia.org/wiki/Canadian_postal_abbreviations_for_provinces_and_territories#Names_and_abbreviations
    CANADA(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Regions_of_Chile
    CHILE(true, false, false, false, false, false, false),
    CHINA(true, false, false, false, false, false, false),
    COLOMBIA(true, false, false, false, false, false, false),
    CROATIA(true, false, false, false, false, false, false),
    CZECH_REPUBLIC(true, false, false, false, false, false, false),
    DENMARK(true, false, false, false, false, false, false),
    ESTONIA(true, false, false, false, false, false, false),
    FINLAND(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Postal_codes_in_France#CEDEX
    FRANCE(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Postal_codes_in_Germany
    GERMANY(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Postal_codes_in_Greece
    GREECE(true, false, false, false, false, false, false),
    HONG_KONG(true, false, false, false, false, false, false),
    HUNGARY(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Postal_codes_in_Iceland
    ICELAND(true, false, false, false, false, false, false),
    INDIA(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Subdivisions_of_Indonesia
    INDONESIA(true, false, false, false, false, false, false),
    IRAN(true, false, false, false, false, false, false),
    IRAQ(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Postal_addresses_in_the_Republic_of_Ireland
    IRELAND(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Postal_codes_in_Israel
    ISRAEL(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Provinces_of_Italy#List_of_provinces
    ITALY(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Japanese_addressing_system
    JAPAN(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Dative_case
    LATVIA(true, false, false, false, false, false, false),
    MACAO(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Pos_Malaysia
    // https://en.wikipedia.org/wiki/Postal_codes_in_Malaysia
    MALAYSIA(true, false, false, false, false, false, false),
    MEXICO(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Postal_codes_in_the_Netherlands
    // https://en.wikipedia.org/wiki/PostNL
    NETHERLANDS(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Postcodes_in_New_Zealand#Examples
    NEW_ZEALAND(true, false, false, false, false, false, false),
    NORWAY(true, false, false, false, false, false, false),
    OMAN(true, false, false, false, false, false, false),
    PAKISTAN(true, false, false, false, false, false, false),
    PERU(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Postal_addresses_in_the_Philippines
    PHILIPPINES(true, false, false, false, false, false, false),
    POLAND(true, false, false, false, false, false, false),
    PORTUGAL(true, false, false, false, false, false, false),
    QATAR(true, false, false, false, false, false, false),
    ROMANIA(true, false, false, false, false, false, false),
    RUSSIA(true, false, false, false, false, false, false),
    SAUDI_ARABIA(true, false, false, false, false, false, false),
    SERBIA(true, false, false, false, false, false, false),
    SINGAPORE(true, false, false, false, false, false, false),
    SLOVAKIA(true, false, false, false, false, false, false),
    SLOVENIA(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Addresses_in_South_Korea
    SOUTH_KOREA(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Provinces_of_Spain#Provinces
    SPAIN(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Sri_Lanka_Post
    SRI_LANKA(true, false, false, false, false, false, false),
    SWEDEN(true, false, false, false, false, false, false),
    SWITZERLAND(true, false, false, false, false, false, false),
    TAIWAN(true, false, false, false, false, false, false),
    THAILAND(true, false, false, false, false, false, false),
    TURKEY(true, false, false, false, false, false, false),
    UKRAINE(true, false, false, false, false, false, false),
    UNITED_ARAB_EMIRATES(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/Postcodes_in_the_United_Kingdom
    // https://en.wikipedia.org/wiki/Postal_counties_of_the_United_Kingdom
    UNITED_KINGDOM(true, false, false, false, false, false, false),
    // https://en.wikipedia.org/wiki/List_of_U.S._state_and_territory_abbreviations
    // https://en.wikipedia.org/wiki/ZIP_Code
    // https://en.wikipedia.org/wiki/United_States_Postal_Service
    UNITED_STATES(true, false, false, false, false, false, false),
    VIETNAM(true, false, false, false, false, false, false)


    AddressFormatSettings(
            boolean houseNumberAfterStreetName,
            boolean appendIso3166ToPostalCode,
            boolean postalCodeBeforeLocality,
            boolean lastLineCapitalLetters,
            boolean unitSlashSeparated,
            boolean unitsPriorToStreetNumber,
            boolean twoSpaceAbbreviatedStateOrSuburb
    ) {
        this.houseNumberAfterStreetName = houseNumberAfterStreetName
        this.appendIso3166ToPostalCode = appendIso3166ToPostalCode
        this.postalCodeBeforeLocality = postalCodeBeforeLocality
    }

    boolean houseNumberAfterStreetName
    boolean appendIso3166ToPostalCode
    boolean postalCodeBeforeLocality
    boolean lastLineCapitalLetters
    boolean unitSlashSeparated
    boolean unitsPriorToStreetNumber
    boolean twoSpaceAbbreviatedStateOrSuburb

    String convertTo(
            Country origin,
            Country destination,
            String building,
            String number,
            String street,
            String street2,
            String street3,
            String city,
            String zip,
            String state,
            Country country,
            String stateCode) {

    }
}

@CompileStatic
enum AddressFieldsSequence {
    // TODO: Complete cases from the listing up there
    DEFAULT_ONE(Fields.NUMBER, Fields.STREET, Fields.BUILDING, Fields.CITY, Fields.ZIP, Fields.STATE, Fields.COUNTRY)

    enum Fields {
        BUILDING, NUMBER, STREET, CITY, ZIP, STATE, COUNTRY, LINE, COMMA
    }

    AddressFieldsSequence(final Fields[] fields) {
        this.fields = fields
    }

    final Fields[] fields

    String printAddress(
            Country from,
            Country to,
            String building,
            String number,
            String street,
            String street2,
            String street3,
            String city,
            String zip,
            String state,
            Country country,
            String stateCode) {
        // TODO: DO THE WORK
        ""
    }
}

interface StateBase {
    String getAbbrev()

    String getName()
}

// TODO: Need the same, first for all the biggest places (China, USA, India, Russia ...)
enum UsaStates implements StateBase {
    TST("Test", "TST")

    UsaStates(String name, String abbrev) {
        this.name = name
        this.abbrev = abbrev
    }

    String name
    String abbrev

}