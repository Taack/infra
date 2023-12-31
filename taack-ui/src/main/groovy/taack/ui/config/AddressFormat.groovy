package taack.ui.config

import groovy.transform.CompileStatic

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

@CompileStatic
interface StateBase {
    Country getCountry()
    String getAbbrev()
    String getName()
}

/*
    credit to Google Bard and ChatGPT for next lines
*/

@CompileStatic
enum UsaStates implements StateBase {
    ALABAMA("AL", "Alabama"),
    ALASKA("AK", "Alaska"),
    ARIZONA("AZ", "Arizona"),
    ARKANSAS("AR", "Arkansas"),
    CALIFORNIA("CA", "California"),
    COLORADO("CO", "Colorado"),
    CONNECTICUT("CT", "Connecticut"),
    DELAWARE("DE", "Delaware"),
    FLORIDA("FL", "Florida"),
    GEORGIA("GA", "Georgia"),
    HAWAII("HI", "Hawaii"),
    IDAHO("ID", "Idaho"),
    ILLINOIS("IL", "Illinois"),
    INDIANA("IN", "Indiana"),
    IOWA("IA", "Iowa"),
    KANSAS("KS", "Kansas"),
    KENTUCKY("KY", "Kentucky"),
    LOUISIANA("LA", "Louisiana"),
    MAINE("ME", "Maine"),
    MARYLAND("MD", "Maryland"),
    MASSACHUSETTS("MA", "Massachusetts"),
    MICHIGAN("MI", "Michigan"),
    MINNESOTA("MN", "Minnesota"),
    MISSISSIPPI("MS", "Mississippi"),
    MISSOURI("MO", "Missouri"),
    MONTANA("MT", "Montana"),
    NEBRASKA("NE", "Nebraska"),
    NEVADA("NV", "Nevada"),
    NEW_HAMPSHIRE("NH", "New Hampshire"),
    NEW_JERSEY("NJ", "New Jersey"),
    NEW_MEXICO("NM", "New Mexico"),
    NEW_YORK("NY", "New York"),
    NORTH_CAROLINA("NC", "North Carolina"),
    NORTH_DAKOTA("ND", "North Dakota"),
    OHIO("OH", "Ohio"),
    OKLAHOMA("OK", "Oklahoma"),
    OREGON("OR", "Oregon"),
    PENNSYLVANIA("PA", "Pennsylvania"),
    RHODE_ISLAND("RI", "Rhode Island"),
    SOUTH_CAROLINA("SC", "South Carolina"),
    SOUTH_DAKOTA("SD", "South Dakota"),
    TENNESSEE("TN", "Tennessee"),
    TEXAS("TX", "Texas"),
    UTAH("UT", "Utah"),
    VERMONT("VT", "Vermont"),
    VIRGINIA("VA", "Virginia"),
    WASHINGTON("WA", "Washington"),
    WEST_VIRGINIA("WV", "West Virginia"),
    WISCONSIN("WI", "Wisconsin"),
    WYOMING("WY", "Wyoming")

    UsaStates(String name, String abbrev) {
        this.name = name
        this.abbrev = abbrev
    }

    Country getCountry() {
        Country.US
    }

    final String abbrev
    final String name
}

@CompileStatic
enum IndianStates implements StateBase {
    AP("Andhra Pradesh", "AP"),
    AR("Arunachal Pradesh", "AR"),
    AS("Assam", "AS"),
    BR("Bihar", "BR"),
    CG("Chhattisgarh", "CG"),
    GA("Goa", "GA"),
    GJ("Gujarat", "GJ"),
    HR("Haryana", "HR"),
    HP("Himachal Pradesh", "HP"),
    JH("Jharkhand", "JH"),
    KA("Karnataka", "KA"),
    KL("Kerala", "KL"),
    MP("Madhya Pradesh", "MP"),
    MH("Maharashtra", "MH"),
    MN("Manipur", "MN"),
    ML("Meghalaya", "ML"),
    MZ("Mizoram", "MZ"),
    NL("Nagaland", "NL"),
    OD("Odisha", "OD"),
    PB("Punjab", "PB"),
    RJ("Rajasthan", "RJ"),
    SK("Sikkim", "SK"),
    TN("Tamil Nadu", "TN"),
    TS("Telangana", "TS"),
    TR("Tripura", "TR"),
    UP("Uttar Pradesh", "UP"),
    UK("Uttarakhand", "UK"),
    WB("West Bengal", "WB")

    IndianStates(String name, String abbrev) {
        this.name = name
        this.abbrev = abbrev
    }

    Country getCountry() {
        Country.IN
    }

    final String abbrev
    final String name
}

@CompileStatic
enum CanadaStates implements StateBase {
    ALBERTA("AB", "Alberta"),
    BRITISH_COLUMBIA("BC", "British Columbia"),
    MANITOBA("MB", "Manitoba"),
    NEW_BRUNSWICK("NB", "New Brunswick"),
    NEWFOUNDLAND_AND_LABRADOR("NL", "Newfoundland and Labrador"),
    NORTHWEST_TERRITORIES("NT", "Northwest Territories"),
    NOVA_SCOTIA("NS", "Nova Scotia"),
    NUNAVUT("NU", "Nunavut"),
    ONTARIO("ON", "Ontario"),
    PRINCE_EDWARD_ISLAND("PE", "Prince Edward Island"),
    QUEBEC("QC", "Quebec"),
    SASKATCHEWAN("SK", "Saskatchewan"),
    YUKON("YT", "Yukon")

    CanadaStates(String name, String abbrev) {
        this.name = name
        this.abbrev = abbrev
    }

    Country getCountry() {
        Country.CA
    }

    final String abbrev
    final String name
}

@CompileStatic
enum ChinaStates implements StateBase {
    ANHUI("AH", "Anhui"),
    BEIJING("BJ", "Beijing"),
    CHONGQING("CQ", "Chongqing"),
    FUJIAN("FJ", "Fujian"),
    GANSU("GS", "Gansu"),
    GUANGDONG("GD", "Guangdong"),
    GUANGXI("GX", "Guangxi"),
    GUIZHOU("GZ", "Guizhou"),
    HAINAN("HI", "Hainan"),
    HEBEI("HE", "Hebei"),
    HEILONGJIANG("HL", "Heilongjiang"),
    HENAN("HA", "Henan"),
    HUBEI("HB", "Hubei"),
    HUNAN("HN", "Hunan"),
    JIANGSU("JS", "Jiangsu"),
    JIANGXI("JX", "Jiangxi"),
    JILIN("JL", "Jilin"),
    LIAONING("LN", "Liaoning"),
    NEI_MONGOL("NM", "Nei Mongol"),
    NINGXIA("NX", "Ningxia"),
    QINGHAI("QH", "Qinghai"),
    SHAANXI("SN", "Shaanxi"),
    SHANDONG("SD", "Shandong"),
    SHANGHAI("SH", "Shanghai"),
    SHANXI("SX", "Shanxi"),
    SICHUAN("SC", "Sichuan"),
    TIANJIN("TJ", "Tianjin"),
    XINJIANG("XJ", "Xinjiang"),
    XIZANG("XZ", "Xizang"),
    YUNNAN("YN", "Yunnan"),
    ZHEJIANG("ZJ", "Zhejiang")

    ChinaStates(String name, String abbrev) {
        this.name = name
        this.abbrev = abbrev
    }

    Country getCountry() {
        Country.CN
    }

    final String abbrev
    final String name
}

@CompileStatic
enum MexicoStates implements StateBase {
    AGUASCALIENTES("AGU", "Aguascalientes"),
    BAJA_CALIFORNIA("BCN", "Baja California"),
    BAJA_CALIFORNIA_SUR("BCS", "Baja California Sur"),
    CAMPECHE("CAM", "Campeche"),
    CHIAPAS("CHP", "Chiapas"),
    CHIHUAHUA("CHH", "Chihuahua"),
    COAHUILA("COA", "Coahuila"),
    COLIMA("COL", "Colima"),
    DURANGO("DUR", "Durango"),
    GUANAJUATO("GUA", "Guanajuato"),
    GUERRERO("GRO", "Guerrero"),
    HIDALGO("HID", "Hidalgo"),
    JALISCO("JAL", "Jalisco"),
    MEXICO("MEX", "México"),
    MICHOACAN("MIC", "Michoacán"),
    MORELOS("MOR", "Morelos"),
    NAYARIT("NAY", "Nayarit"),
    NUEVO_LEON("NLE", "Nuevo León"),
    OAXACA("OAX", "Oaxaca"),
    PUEBLA("PUE", "Puebla"),
    QUERETARO("QUE", "Querétaro"),
    QUINTANA_ROO("ROO", "Quintana Roo"),
    SAN_LUIS_POTOSI("SLP", "San Luis Potosí"),
    SINALOA("SIN", "Sinaloa"),
    SONORA("SON", "Sonora"),
    TABASCO("TAB", "Tabasco"),
    TAMAULIPAS("TAM", "Tamaulipas"),
    TLAXCALA("TLA", "Tlaxcala"),
    VERACRUZ("VER", "Veracruz"),
    YUCATAN("YUC", "Yucatán"),
    ZACATECAS("ZAC", "Zacatecas")

    MexicoStates(String name, String abbrev) {
        this.name = name
        this.abbrev = abbrev
    }

    Country getCountry() {
        Country.CN
    }

    final String abbrev
    final String name
}

@CompileStatic
final class ManagedStates {

    static StateBase[] getStates(Country country) {
        switch (country) {
            case Country.US:
                return UsaStates.values()
            case Country.CA:
                return CanadaStates.values()
            case Country.CN:
                return ChinaStates.values()
            case Country.ME:
                return MexicoStates.values()
        }
        return null
    }

    static StateBase getState(Country country, String stateAbbrev) {
        getStates(country).find {
            it.abbrev == stateAbbrev
        }
    }

}
