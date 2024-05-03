/**
 * Various default Helper classes
 */
package taack.ui.config

import groovy.transform.CompileStatic
import taack.ui.IEnumOption

@CompileStatic
final enum Continent {
    AF('Africa', 30_065_000, 1_216_130_000),
    AN('Antarctica', 13_209_000, 1_500),
    AS('Asia', 44_579_000, 4_436_224_000),
    EU('Europe', 9_938_000, 738_849_000),
    NA('North America', 24_256_000, 410_013_492),
    OC('Oceania', 7_687_000, 39_901_000),
    SA('South America', 17_819_000, 410_013_492)

    Continent(final String name, final int areaKm2, final long population) {
        this.name = name
        this.areaKm2 = areaKm2
        this.population = population
    }

    final String name
    final int areaKm2
    final long population
}

@CompileStatic
final enum ContinentCountries {
    DZ(Continent.AF),
    AO(Continent.AF),
    BW(Continent.AF),
    IO(Continent.AF),
    BI(Continent.AF),
    CM(Continent.AF),
    CV(Continent.AF),
    CF(Continent.AF),
    TD(Continent.AF),
    KM(Continent.AF),
    YT(Continent.AF),
    CG(Continent.AF),
    CD(Continent.AF),
    BJ(Continent.AF),
    GQ(Continent.AF),
    ET(Continent.AF),
    ER(Continent.AF),
    TF(Continent.AF),
    DJ(Continent.AF),
    GA(Continent.AF),
    GM(Continent.AF),
    GH(Continent.AF),
    GN(Continent.AF),
    CI(Continent.AF),
    KE(Continent.AF),
    LS(Continent.AF),
    LR(Continent.AF),
    LY(Continent.AF),
    MG(Continent.AF),
    MW(Continent.AF),
    ML(Continent.AF),
    MR(Continent.AF),
    MU(Continent.AF),
    MA(Continent.AF),
    MZ(Continent.AF),
    NA(Continent.AF),
    NE(Continent.AF),
    NG(Continent.AF),
    GW(Continent.AF),
    RE(Continent.AF),
    RW(Continent.AF),
    SH(Continent.AF),
    ST(Continent.AF),
    SN(Continent.AF),
    SC(Continent.AF),
    SL(Continent.AF),
    SO(Continent.AF),
    ZA(Continent.AF),
    ZW(Continent.AF),
    SS(Continent.AF),
    SD(Continent.AF),
    EH(Continent.AF),
    SZ(Continent.AF),
    TG(Continent.AF),
    TN(Continent.AF),
    UG(Continent.AF),
    TZ(Continent.AF),
    BF(Continent.AF),
    ZM(Continent.AF),
    AQ(Continent.AN),
    BV(Continent.AN),
    GS(Continent.AN),
    HM(Continent.AN),
    AF(Continent.AS),
    BH(Continent.AS),
    BD(Continent.AS),
    BT(Continent.AS),
    BN(Continent.AS),
    MM(Continent.AS),
    KH(Continent.AS),
    LK(Continent.AS),
    CN(Continent.AS),
    TW(Continent.AS),
    CX(Continent.AS),
    CC(Continent.AS),
    HK(Continent.AS),
    IN(Continent.AS),
    ID(Continent.AS),
    IR(Continent.AS),
    IQ(Continent.AS),
    IL(Continent.AS),
    JP(Continent.AS),
    JO(Continent.AS),
    KP(Continent.AS),
    KR(Continent.AS),
    KW(Continent.AS),
    KG(Continent.AS),
    LA(Continent.AS),
    LB(Continent.AS),
    MO(Continent.AS),
    MY(Continent.AS),
    MV(Continent.AS),
    MN(Continent.AS),
    OM(Continent.AS),
    NP(Continent.AS),
    PK(Continent.AS),
    PS(Continent.AS),
    PH(Continent.AS),
    TL(Continent.AS),
    QA(Continent.AS),
    SA(Continent.AS),
    SG(Continent.AS),
    VN(Continent.AS),
    SY(Continent.AS),
    TJ(Continent.AS),
    TH(Continent.AS),
    AE(Continent.AS),
    TM(Continent.AS),
    EG(Continent.AS),
    UZ(Continent.AS),
    YE(Continent.AS),
    XD(Continent.AS),
    XS(Continent.AS),
    AL(Continent.EU),
    AD(Continent.EU),
    AZ(Continent.EU),
    AT(Continent.EU),
    AM(Continent.EU),
    BE(Continent.EU),
    BA(Continent.EU),
    BG(Continent.EU),
    BY(Continent.EU),
    HR(Continent.EU),
    CY(Continent.EU),
    CZ(Continent.EU),
    DK(Continent.EU),
    EE(Continent.EU),
    FO(Continent.EU),
    FI(Continent.EU),
    AX(Continent.EU),
    FR(Continent.EU),
    GE(Continent.EU),
    DE(Continent.EU),
    GI(Continent.EU),
    GR(Continent.EU),
    VA(Continent.EU),
    HU(Continent.EU),
    IS(Continent.EU),
    IE(Continent.EU),
    IT(Continent.EU),
    KZ(Continent.EU),
    XK(Continent.EU),
    LV(Continent.EU),
    LI(Continent.EU),
    LT(Continent.EU),
    LU(Continent.EU),
    MT(Continent.EU),
    MC(Continent.EU),
    MD(Continent.EU),
    ME(Continent.EU),
    NL(Continent.EU),
    NO(Continent.EU),
    PL(Continent.EU),
    FL(Continent.EU),
    PT(Continent.EU),
    RO(Continent.EU),
    RU(Continent.EU),
    SM(Continent.EU),
    RS(Continent.EU),
    SK(Continent.EU),
    SI(Continent.EU),
    ES(Continent.EU),
    SJ(Continent.EU),
    SE(Continent.EU),
    CH(Continent.EU),
    TR(Continent.EU),
    UA(Continent.EU),
    MK(Continent.EU),
    GB(Continent.EU),
    GG(Continent.EU),
    JE(Continent.EU),
    IM(Continent.EU),
    AG(Continent.NA),
    BS(Continent.NA),
    BB(Continent.NA),
    BM(Continent.NA),
    BZ(Continent.NA),
    VG(Continent.NA),
    CA(Continent.NA),
    KY(Continent.NA),
    CR(Continent.NA),
    CU(Continent.NA),
    DM(Continent.NA),
    DO(Continent.NA),
    SV(Continent.NA),
    GL(Continent.NA),
    GD(Continent.NA),
    GP(Continent.NA),
    GT(Continent.NA),
    HT(Continent.NA),
    HN(Continent.NA),
    JM(Continent.NA),
    MQ(Continent.NA),
    MX(Continent.NA),
    MS(Continent.NA),
    CW(Continent.NA),
    AW(Continent.NA),
    SX(Continent.NA),
    BQ(Continent.NA),
    NI(Continent.NA),
    PA(Continent.NA),
    PR(Continent.NA),
    BL(Continent.NA),
    KN(Continent.NA),
    AI(Continent.NA),
    LC(Continent.NA),
    MF(Continent.NA),
    PM(Continent.NA),
    VC(Continent.NA),
    TT(Continent.NA),
    TC(Continent.NA),
    US(Continent.NA),
    VI(Continent.NA),
    AS(Continent.OC),
    AU(Continent.OC),
    SB(Continent.OC),
    CK(Continent.OC),
    FJ(Continent.OC),
    PF(Continent.OC),
    KI(Continent.OC),
    GU(Continent.OC),
    NR(Continent.OC),
    NC(Continent.OC),
    VU(Continent.OC),
    NZ(Continent.OC),
    NU(Continent.OC),
    NF(Continent.OC),
    MP(Continent.OC),
    UM(Continent.OC),
    FM(Continent.OC),
    MH(Continent.OC),
    PW(Continent.OC),
    PG(Continent.OC),
    PN(Continent.OC),
    TK(Continent.OC),
    TO(Continent.OC),
    TV(Continent.OC),
    WF(Continent.OC),
    WS(Continent.OC),
    XX(Continent.OC),
    AR(Continent.SA),
    BO(Continent.SA),
    BR(Continent.SA),
    CL(Continent.SA),
    CO(Continent.SA),
    EC(Continent.SA),
    FK(Continent.SA),
    GF(Continent.SA),
    GY(Continent.SA),
    PY(Continent.SA),
    PE(Continent.SA),
    SR(Continent.SA),
    UY(Continent.SA),
    VE(Continent.SA)

    ContinentCountries(final Continent continent) {
        this.continent = continent
    }

    final Continent continent
}

@CompileStatic
enum Country {
    AD(Continent.EU, "Andorra", "AD", "AND", 20, 42.5f),
    AE(Continent.AS, "United Arab Emirates", "AE", "ARE", 784, 24f),
    AF(Continent.AS, "Afghanistan", "AF", "AFG", 4, 33f),
    AG(Continent.NA, "Antigua and Barbuda", "AG", "ATG", 28, 17.05f),
    AI(Continent.NA, "Anguilla", "AI", "AIA", 660, 18.25f),
    AL(Continent.EU, "Albania", "AL", "ALB", 8, 41f),
    AM(Continent.EU, "Armenia", "AM", "ARM", 51, 40f),
    AO(Continent.AF, "Netherlands Antilles", "AN", "ANT", 530, 12.25f),
    AQ(Continent.AN, "Angola", "AO", "AGO", 24, -12.5f),
    AR(Continent.SA, "Antarctica", "AQ", "ATA", 10, -90f),
    AS(Continent.OC, "Argentina", "AR", "ARG", 32, -34f),
    AT(Continent.EU, "American Samoa", "AS", "ASM", 16, -14.3333f),
    AU(Continent.OC, "Austria", "AT", "AUT", 40, 47.3333f),
    AW(Continent.NA, "Australia", "AU", "AUS", 36, -27f),
    AX(Continent.EU, "Aruba", "AW", "ABW", 533, 12.5f),
    AZ(Continent.EU, "Azerbaijan", "AZ", "AZE", 31, 40.5f),
    BA(Continent.EU, "Bosnia and Herzegovina", "BA", "BIH", 70, 44f),
    BB(Continent.NA, "Barbados", "BB", "BRB", 52, 13.1667f),
    BD(Continent.AS, "Bangladesh", "BD", "BGD", 50, 24f),
    BE(Continent.EU, "Belgium", "BE", "BEL", 56, 50.8333f),
    BF(Continent.AF, "Burkina Faso", "BF", "BFA", 854, 13f),
    BG(Continent.EU, "Bulgaria", "BG", "BGR", 100, 43f),
    BH(Continent.AS, "Bahrain", "BH", "BHR", 48, 26f),
    BI(Continent.AF, "Burundi", "BI", "BDI", 108, -3.5f),
    BJ(Continent.AF, "Benin", "BJ", "BEN", 204, 9.5f),
    BL(Continent.NA, "Saint-Barthélemy", "BL", "BLM", 1, 0.0f),
    BM(Continent.NA, "Bermuda", "BM", "BMU", 60, 32.3333f),
    BN(Continent.AS, "Brunei", "BN", "BRN", 96, 4.5f),
    BO(Continent.SA, "Bolivia", "BO", "BOL", 68, -17f),
    BQ(Continent.NA, "Caribbean Netherlands", "BQ", "BES", 68, -17f),
    BR(Continent.SA, "Brazil", "BR", "BRA", 76, -10f),
    BS(Continent.NA, "Bahamas", "BS", "BHS", 44, 24.25f),
    BT(Continent.AS, "Bhutan", "BT", "BTN", 64, 27.5f),
    BV(Continent.AN, "Bouvet Island", "BV", "BVT", 74, -54.4333f),
    BW(Continent.AF, "Botswana", "BW", "BWA", 72, -22f),
    BY(Continent.EU, "Belarus", "BY", "BLR", 112, 53f),
    BZ(Continent.NA, "Belize", "BZ", "BLZ", 84, 17.25f),
    CA(Continent.NA, "Canada", "CA", "CAN", 124, 60f),
    CC(Continent.AS, "Cocos (Keeling), Islands", "CC", "CCK", 166, -12.5f),
    CD(Continent.AF, '"Congo, the Democratic Republic of the"', "CD", "COD", 180, 0f),
    CF(Continent.AF, "Central African Republic", "CF", "CAF", 140, 7f),
    CG(Continent.AF, "Congo", "CG", "COG", 178, -1f),
    CH(Continent.EU, "Switzerland", "CH", "CHE", 756, 47f),
    CI(Continent.AF, "Ivory Coast", "CI", "CIV", 384, 8f),
    CK(Continent.OC, "Cook Islands", "CK", "COK", 184, -21.2333f),
    CL(Continent.SA, "Chile", "CL", "CHL", 152, -30f),
    CM(Continent.AF, "Cameroon", "CM", "CMR", 120, 6f),
    CN(Continent.AS, "China", "CN", "CHN", 156, 35f),
    CO(Continent.SA, "Colombia", "CO", "COL", 170, 4f),
    CR(Continent.NA, "Costa Rica", "CR", "CRI", 188, 10f),
    CS(Continent.EU, "Czechoslovakia", "CS", "CSK", 132, 16f),
    CU(Continent.NA, "Cuba", "CU", "CUB", 192, 21.5f),
    CV(Continent.AF, "Cape Verde", "CV", "CPV", 132, 16f),
    CW(Continent.NA, "Curaçao", "CW", "CUW", 170, 4f),
    CX(Continent.AS, "Christmas Island", "CX", "CXR", 162, -10.5f),
    CY(Continent.EU, "Cyprus", "CY", "CYP", 196, 35f),
    CZ(Continent.EU, "Czech Republic", "CZ", "CZE", 203, 49.75f),
    DE(Continent.EU, "Germany", "DE", "DEU", 276, 51f),
    DJ(Continent.AF, "Djibouti", "DJ", "DJI", 262, 11.5f),
    DK(Continent.EU, "Denmark", "DK", "DNK", 208, 56f),
    DM(Continent.NA, "Dominica", "DM", "DMA", 212, 15.4167f),
    DO(Continent.NA, "Dominican Republic", "DO", "DOM", 214, 19f),
    DZ(Continent.AF, "Algeria", "DZ", "DZA", 12, 28f),
    EC(Continent.SA, "Ecuador", "EC", "ECU", 218, -2f),
    EE(Continent.EU, "Estonia", "EE", "EST", 233, 59f),
    EG(Continent.AS, "Egypt", "EG", "EGY", 818, 27f),
    EH(Continent.AF, "Western Sahara", "EH", "ESH", 732, 24.5f),
    ER(Continent.AF, "Eritrea", "ER", "ERI", 232, 15f),
    ES(Continent.EU, "Spain", "ES", "ESP", 724, 40f),
    ET(Continent.AF, "Ethiopia", "ET", "ETH", 231, 8f),
    FI(Continent.EU, "Finland", "FI", "FIN", 246, 64f),
    FJ(Continent.OC, "Fiji", "FJ", "FJI", 242, -18f),
    FK(Continent.SA, "Falkland Islands (Malvinas),", "FK", "FLK", 238, -51.75f),
    FL(Continent.EU, "Liechtenstein", "FL", "LIE", 47, 9.55f),
    FM(Continent.OC, '"Micronesia, Federated States of"', "FM", "FSM", 583, 6.9167f),
    FO(Continent.EU, "Faroe Islands", "FO", "FRO", 234, 62f),
    FR(Continent.EU, "France", "FR", "FRA", 250, 46f),
    GA(Continent.AF, "Gabon", "GA", "GAB", 266, -1f),
    GB(Continent.EU, "United Kingdom", "GB", "GBR", 826, 54f),
    GD(Continent.NA, "Grenada", "GD", "GRD", 308, 12.1167f),
    GE(Continent.EU, "Georgia", "GE", "GEO", 268, 42f),
    GF(Continent.SA, "French Guiana", "GF", "GUF", 254, 4f),
    GG(Continent.EU, "Guernsey", "GG", "GGY", 831, 49.5f),
    GH(Continent.AF, "Ghana", "GH", "GHA", 288, 8f),
    GI(Continent.EU, "Gibraltar", "GI", "GIB", 292, 36.1833f),
    GL(Continent.NA, "Greenland", "GL", "GRL", 304, 72f),
    GM(Continent.AF, "Gambia", "GM", "GMB", 270, 13.4667f),
    GN(Continent.AF, "Guinea", "GN", "GIN", 324, 11f),
    GP(Continent.NA, "Guadeloupe", "GP", "GLP", 312, 16.25f),
    GQ(Continent.AF, "Equatorial Guinea", "GQ", "GNQ", 226, 2f),
    GR(Continent.EU, "Greece", "GR", "GRC", 300, 39f),
    GS(Continent.AN, "South Georgia and the South Sandwich Islands", "GS", "SGS", 239, -54.5f),
    GT(Continent.NA, "Guatemala", "GT", "GTM", 320, 15.5f),
    GU(Continent.OC, "Guam", "GU", "GUM", 316, 13.4667f),
    GW(Continent.AF, "Guinea-Bissau", "GW", "GNB", 624, 12f),
    GY(Continent.SA, "Guyana", "GY", "GUY", 328, 5f),
    HK(Continent.AS, "Hong Kong", "HK", "HKG", 344, 22.25f),
    HM(Continent.AN, "Heard Island and McDonald Islands", "HM", "HMD", 334, -53.1f),
    HN(Continent.NA, "Honduras", "HN", "HND", 340, 15f),
    HR(Continent.EU, "Croatia", "HR", "HRV", 191, 45.1667f),
    HT(Continent.NA, "Haiti", "HT", "HTI", 332, 19f),
    HU(Continent.EU, "Hungary", "HU", "HUN", 348, 47f),
    ID(Continent.AS, "Indonesia", "ID", "IDN", 360, -5f),
    IE(Continent.EU, "Ireland", "IE", "IRL", 372, 53f),
    IL(Continent.AS, "Israel", "IL", "ISR", 376, 31.5f),
    IM(Continent.EU, "Isle of Man", "IM", "IMN", 833, 54.23f),
    IN(Continent.AS, "India", "IN", "IND", 356, 20f),
    IO(Continent.AF, "British Indian Ocean Territory", "IO", "IOT", 86, -6f),
    IQ(Continent.AS, "Iraq", "IQ", "IRQ", 368, 33f),
    IR(Continent.AS, '"Iran, Islamic Republic of"', "IR", "IRN", 364, 32f),
    IS(Continent.EU, "Iceland", "IS", "ISL", 352, 65f),
    IT(Continent.EU, "Italy", "IT", "ITA", 380, 42.8333f),
    JE(Continent.EU, "Jersey", "JE", "JEY", 832, 49.21f),
    JM(Continent.NA, "Jamaica", "JM", "JAM", 388, 18.25f),
    JO(Continent.AS, "Jordan", "JO", "JOR", 400, 31f),
    JP(Continent.AS, "Japan", "JP", "JPN", 392, 36f),
    KE(Continent.AF, "Kenya", "KE", "KEN", 404, 1f),
    KG(Continent.AS, "Kyrgyzstan", "KG", "KGZ", 417, 41f),
    KH(Continent.AS, "Cambodia", "KH", "KHM", 116, 13f),
    KI(Continent.OC, "Kiribati", "KI", "KIR", 296, 1.4167f),
    KM(Continent.AF, "Comoros", "KM", "COM", 174, -12.1667f),
    KN(Continent.NA, "Saint Kitts and Nevis", "KN", "KNA", 659, 17.3333f),
    KP(Continent.AS, "Korea, Democratic People's Republic of", "KP", "PRK", 408, 40f),
    KR(Continent.AS, "South Korea", "KR", "KOR", 410, 37f),
    KW(Continent.AS, "Kuwait", "KW", "KWT", 414, 29.3375f),
    KY(Continent.NA, "Cayman Islands", "KY", "CYM", 136, 19.5f),
    KZ(Continent.EU, "Kazakhstan", "KZ", "KAZ", 398, 48f),
    LA(Continent.AS, "Lao People's Democratic Republic", "LA", "LAO", 418, 18f),
    LB(Continent.AS, "Lebanon", "LB", "LBN", 422, 33.8333f),
    LC(Continent.NA, "Saint Lucia", "LC", "LCA", 662, 13.8833f),
    LI(Continent.EU, "Liechtenstein", "LI", "LIE", 438, 47.1667f),
    LK(Continent.AS, "Sri Lanka", "LK", "LKA", 144, 7f),
    LR(Continent.AF, "Liberia", "LR", "LBR", 430, 6.5f),
    LS(Continent.AF, "Lesotho", "LS", "LSO", 426, -29.5f),
    LT(Continent.EU, "Lithuania", "LT", "LTU", 440, 56f),
    LU(Continent.EU, "Luxembourg", "LU", "LUX", 442, 49.75f),
    LV(Continent.EU, "Latvia", "LV", "LVA", 428, 57f),
    LY(Continent.AF, "Libya", "LY", "LBY", 434, 25f),
    MA(Continent.AF, "Morocco", "MA", "MAR", 504, 32f),
    MC(Continent.EU, "Monaco", "MC", "MCO", 492, 43.7333f),
    MD(Continent.EU, '"Moldova, Republic of"', "MD", "MDA", 498, 47f),
    ME(Continent.EU, "Montenegro", "ME", "MNE", 499, 42f),
    MF(Continent.NA, "Saint Martin", "MF", "MAF", 99, 0.0f),
    MG(Continent.AF, "Madagascar", "MG", "MDG", 450, -20f),
    MH(Continent.OC, "Marshall Islands", "MH", "MHL", 584, 9f),
    MK(Continent.EU, '"Macedonia, the former Yugoslav Republic of"', "MK", "MKD", 807, 41.8333f),
    ML(Continent.AF, "Mali", "ML", "MLI", 466, 17f),
    MM(Continent.AS, "Burma", "MM", "MMR", 104, 22f),
    MN(Continent.AS, "Mongolia", "MN", "MNG", 496, 46f),
    MO(Continent.AS, "Macao", "MO", "MAC", 446, 22.1667f),
    MP(Continent.OC, "Northern Mariana Islands", "MP", "MNP", 580, 15.2f),
    MQ(Continent.NA, "Martinique", "MQ", "MTQ", 474, 14.6667f),
    MR(Continent.AF, "Mauritania", "MR", "MRT", 478, 20f),
    MS(Continent.NA, "Montserrat", "MS", "MSR", 500, 16.75f),
    MT(Continent.EU, "Malta", "MT", "MLT", 470, 35.8333f),
    MU(Continent.AF, "Mauritius", "MU", "MUS", 480, -20.2833f),
    MV(Continent.AS, "Maldives", "MV", "MDV", 462, 3.25f),
    MW(Continent.AF, "Malawi", "MW", "MWI", 454, -13.5f),
    MX(Continent.NA, "Mexico", "MX", "MEX", 484, 23f),
    MY(Continent.AS, "Malaysia", "MY", "MYS", 458, 2.5f),
    MZ(Continent.AF, "Mozambique", "MZ", "MOZ", 508, -18.25f),
    NA(Continent.AF, "Namibia", "NA", "NAM", 516, -22f),
    NC(Continent.OC, "New Caledonia", "NC", "NCL", 540, -21.5f),
    NE(Continent.AF, "Niger", "NE", "NER", 562, 16f),
    NF(Continent.OC, "Norfolk Island", "NF", "NFK", 574, -29.0333f),
    NG(Continent.AF, "Nigeria", "NG", "NGA", 566, 10f),
    NI(Continent.NA, "Nicaragua", "NI", "NIC", 558, 13f),
    NL(Continent.EU, "Netherlands", "NL", "NLD", 528, 52.5f),
    NO(Continent.EU, "Norway", "NO", "NOR", 578, 62f),
    NP(Continent.AS, "Nepal", "NP", "NPL", 524, 28f),
    NR(Continent.OC, "Nauru", "NR", "NRU", 520, -0.5333f),
    NU(Continent.OC, "Niue", "NU", "NIU", 570, -19.0333f),
    NZ(Continent.OC, "New Zealand", "NZ", "NZL", 554, -41f),
    OM(Continent.AS, "Oman", "OM", "OMN", 512, 21f),
    PA(Continent.NA, "Panama", "PA", "PAN", 591, 9f),
    PE(Continent.SA, "Peru", "PE", "PER", 604, -10f),
    PF(Continent.OC, "French Polynesia", "PF", "PYF", 258, -15f),
    PG(Continent.OC, "Papua New Guinea", "PG", "PNG", 598, -6f),
    PH(Continent.AS, "Philippines", "PH", "PHL", 608, 13f),
    PK(Continent.AS, "Pakistan", "PK", "PAK", 586, 30f),
    PL(Continent.EU, "Poland", "PL", "POL", 616, 52f),
    PM(Continent.NA, "Saint Pierre and Miquelon", "PM", "SPM", 666, 46.8333f),
    PN(Continent.OC, "Pitcairn", "PN", "PCN", 612, -24.7f),
    PR(Continent.NA, "Puerto Rico", "PR", "PRI", 630, 18.25f),
    PS(Continent.AS, '"Palestinian Territory, Occupied"', "PS", "PSE", 275, 32f),
    PT(Continent.EU, "Portugal", "PT", "PRT", 620, 39.5f),
    PW(Continent.OC, "Palau", "PW", "PLW", 585, 7.5f),
    PY(Continent.SA, "Paraguay", "PY", "PRY", 600, -23f),
    QA(Continent.AS, "Qatar", "QA", "QAT", 634, 25.5f),
    RE(Continent.AF, "Réunion", "RE", "REU", 638, -21.1f),
    RO(Continent.EU, "Romania", "RO", "ROU", 642, 46f),
    RS(Continent.EU, "Serbia", "RS", "SRB", 688, 44f),
    RU(Continent.EU, "Russia", "RU", "RUS", 643, 60f),
    RW(Continent.AF, "Rwanda", "RW", "RWA", 646, -2f),
    SA(Continent.AS, "Saudi Arabia", "SA", "SAU", 682, 25f),
    SB(Continent.OC, "Solomon Islands", "SB", "SLB", 90, -8f),
    SC(Continent.AF, "Seychelles", "SC", "SYC", 690, -4.5833f),
    SD(Continent.AF, "Sudan", "SD", "SDN", 736, 15f),
    SE(Continent.EU, "Sweden", "SE", "SWE", 752, 62f),
    SG(Continent.AS, "Singapore", "SG", "SGP", 702, 1.3667f),
    SH(Continent.AF, '"Saint Helena, Ascension and Tristan da Cunha"', "SH", "SHN", 654, -15.9333f),
    SI(Continent.EU, "Slovenia", "SI", "SVN", 705, 46f),
    SJ(Continent.EU, "Svalbard and Jan Mayen", "SJ", "SJM", 744, 78f),
    SK(Continent.EU, "Slovakia", "SK", "SVK", 703, 48.6667f),
    SL(Continent.AF, "Sierra Leone", "SL", "SLE", 694, 8.5f),
    SM(Continent.EU, "San Marino", "SM", "SMR", 674, 43.7667f),
    SN(Continent.AF, "Senegal", "SN", "SEN", 686, 14f),
    SO(Continent.AF, "Somalia", "SO", "SOM", 706, 10f),
    SR(Continent.SA, "Suriname", "SR", "SUR", 740, 4f),
    SS(Continent.AF, "South sudan", "SS", "SSD", 99, 0.0f),
    ST(Continent.AF, "Sao Tome and Principe", "ST", "STP", 678, 1f),
    SV(Continent.NA, "El Salvador", "SV", "SLV", 222, 13.8333f),
    SX(Continent.NA, "Sint Maarten", "SX", "SXM", 99, 0.0f),
    SY(Continent.AS, "Syrian Arab Republic", "SY", "SYR", 760, 35f),
    SZ(Continent.AF, "Swaziland", "SZ", "SWZ", 748, -26.5f),
    TC(Continent.NA, "Turks and Caicos Islands", "TC", "TCA", 796, 21.75f),
    TD(Continent.AF, "Chad", "TD", "TCD", 148, 15f),
    TF(Continent.AF, "French Southern Territories", "TF", "ATF", 260, -43f),
    TG(Continent.AF, "Togo", "TG", "TGO", 768, 8f),
    TH(Continent.AS, "Thailand", "TH", "THA", 764, 15f),
    TJ(Continent.AS, "Tajikistan", "TJ", "TJK", 762, 39f),
    TK(Continent.OC, "Tokelau", "TK", "TKL", 772, -9f),
    TL(Continent.AS, "Timor-Leste", "TL", "TLS", 626, -8.55f),
    TM(Continent.AS, "Turkmenistan", "TM", "TKM", 795, 40f),
    TN(Continent.AF, "Tunisia", "TN", "TUN", 788, 34f),
    TO(Continent.OC, "Tonga", "TO", "TON", 776, -20f),
    TR(Continent.EU, "Turkey", "TR", "TUR", 792, 39f),
    TT(Continent.NA, "Trinidad and Tobago", "TT", "TTO", 780, 11f),
    TV(Continent.OC, "Tuvalu", "TV", "TUV", 798, -8f),
    TW(Continent.AS, "Taiwan", "TW", "TWN", 158, 23.5f),
    TZ(Continent.AF, '"Tanzania, United Republic of"', "TZ", "TZA", 834, -6f),
    UA(Continent.EU, "Ukraine", "UA", "UKR", 804, 49f),
    UG(Continent.AF, "Uganda", "UG", "UGA", 800, 1f),
    UM(Continent.OC, "United States Minor Outlying Islands", "UM", "UMI", 581, 19.2833f),
    US(Continent.NA, "United States of America", "US", "USA", 840, 38f),
    UY(Continent.SA, "Uruguay", "UY", "URY", 858, -33f),
    UZ(Continent.AS, "Uzbekistan", "UZ", "UZB", 860, 41f),
    VA(Continent.EU, "Holy See (Vatican City State),", "VA", "VAT", 336, 41.9f),
    VC(Continent.NA, "St. Vincent and the Grenadines", "VC", "VCT", 670, 13.25f),
    VE(Continent.SA, "Venezuela", "VE", "VEN", 862, 8f),
    VG(Continent.NA, '"Virgin Islands, British"', "VG", "VGB", 92, 18.5f),
    VI(Continent.NA, '"Virgin Islands, U.S."', "VI", "VIR", 850, 18.3333f),
    VN(Continent.AS, "Vietnam", "VN", "VNM", 704, 16f),
    VU(Continent.OC, "Vanuatu", "VU", "VUT", 548, -16f),
    WF(Continent.OC, "Wallis and Futuna", "WF", "WLF", 876, -13.3f),
    WS(Continent.OC, "Samoa", "WS", "WSM", 882, -13.5833f),
    XK(Continent.EU, "Kosovo", "XK", "XKX", 43, 20.9f),
    YE(Continent.AS, "Yemen", "YE", "YEM", 887, 15f),
    YT(Continent.AF, "Mayotte", "YT", "MYT", 175, -12.8333f),
    ZA(Continent.AF, "South Africa", "ZA", "ZAF", 710, -29f),
    ZM(Continent.AF, "Zambia", "ZM", "ZMB", 894, -15f),
    ZW(Continent.AF, "Zimbabwe", "ZW", "ZWE", 716, -20f)

    Country(final Continent continent, final String name, final String alphaIso2, final String alphaIso3, final int latitude, final float longitude) {
        this.continent = continent
        this.name = name
        this.alphaIso2 = alphaIso2
        this.alphaIso3 = alphaIso3
        this.latitude = latitude
        this.longitude = longitude
    }
    final Continent continent
    final String name
    final String alphaIso2
    final String alphaIso3
    final float latitude
    final double longitude

    String getAsset() {
        "/assets/icons/countries/${alphaIso2.toLowerCase()}.webp"
    }

    static Country fromAlphaIso3(final String alphaIso3) {
        if (alphaIso3) {
            final String a3 = alphaIso3.toLowerCase()
            values().find { it.alphaIso3.toLowerCase() == a3 }
        } else null
    }

    static Country fromAlphaIso2(final String alphaIso2) {
        if (alphaIso2) {
            final String a2 = alphaIso2.toLowerCase()
            values().find { it.alphaIso2.toLowerCase() == a2 }
        } else null
    }

    String nameForLanguage(String lang = "en") {
        def locale = new Locale("", this.alphaIso2)
        return locale.getDisplayCountry(new Locale(lang, this.alphaIso2))
    }

    static IEnumOption[] getEnumOptions() {
        IEnumOption[] res = new IEnumOption[values().size() + Continent.values().size()]
        int i = 0

        for (def c in Continent.values()) {
            res[i++] = new IEnumOption() {
                @Override
                String getKey() {
                    return c.toString()
                }

                @Override
                String getValue() {
                    return c.name
                }

                @Override
                String getAsset() {
                    return null
                }

                @Override
                Boolean isSection() {
                    return true
                }
            }
            for (def country in values().findAll { it.continent == c }.sort({ it.name })) {
                res[i++] = new IEnumOption() {
                    @Override
                    String getKey() {
                        return country.alphaIso2
                    }

                    @Override
                    String getValue() {
                        return country.name
                    }

                    @Override
                    String getAsset() {
                        return "taack/icons/countries/4x3/${country.alphaIso2.toLowerCase()}.webp"
                    }

                    @Override
                    Boolean isSection() {
                        return false
                    }
                }
            }
        }

        return res
    }

    @Override
    String toString() {
        name
    }

    String getId() {
        this.name()
    }

}