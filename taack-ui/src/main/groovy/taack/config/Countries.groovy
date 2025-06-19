/**
 * Various default Helper classes
 */
package taack.config

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
enum Country {
    AF(Continent.AS, 'Afghanistan', 'AF', 'AFG', 4, 33f),
    AL(Continent.EU, 'Albania', 'AL', 'ALB', 8, 41f),
    DZ(Continent.AF, 'Algeria', 'DZ', 'DZA', 12, 28f),
    AS(Continent.OC, 'American Samoa', 'AS', 'ASM', 16, -14.3333f),
    AD(Continent.EU, 'Andorra', 'AD', 'AND', 20, 42.5f),
    AO(Continent.AF, 'Angola', 'AO', 'AGO', 24, -12.5f),
    AI(Continent.NA, 'Anguilla', 'AI', 'AIA', 660, 18.25f),
    AQ(Continent.AN, 'Antarctica', 'AQ', 'ATA', 10, -90f),
    AG(Continent.NA, 'Antigua and Barbuda', 'AG', 'ATG', 28, 17.05f),
    AR(Continent.SA, 'Argentina', 'AR', 'ARG', 32, -34f),
    AM(Continent.AS, 'Armenia', 'AM', 'ARM', 51, 40f),
    AW(Continent.NA, 'Aruba', 'AW', 'ABW', 533, 12.5f),
    AU(Continent.OC, 'Australia', 'AU', 'AUS', 36, -27f),
    AT(Continent.EU, 'Austria', 'AT', 'AUT', 40, 47.3333f),
    AZ(Continent.AS, 'Azerbaijan', 'AZ', 'AZE', 31, 40.5f),
    BS(Continent.NA, 'Bahamas', 'BS', 'BHS', 44, 24.25f),
    BH(Continent.AS, 'Bahrain', 'BH', 'BHR', 48, 26f),
    BD(Continent.AS, 'Bangladesh', 'BD', 'BGD', 50, 24f),
    BB(Continent.NA, 'Barbados', 'BB', 'BRB', 52, 13.1667f),
    BY(Continent.EU, 'Belarus', 'BY', 'BLR', 112, 53f),
    BE(Continent.EU, 'Belgium', 'BE', 'BEL', 56, 50.8333f),
    BZ(Continent.NA, 'Belize', 'BZ', 'BLZ', 84, 17.25f),
    BJ(Continent.AF, 'Benin', 'BJ', 'BEN', 204, 9.5f),
    BM(Continent.NA, 'Bermuda', 'BM', 'BMU', 60, 32.3333f),
    BT(Continent.AS, 'Bhutan', 'BT', 'BTN', 64, 27.5f),
    BO(Continent.SA, 'Bolivia', 'BO', 'BOL', 68, -17f),
    BA(Continent.EU, 'Bosnia and Herzegovina', 'BA', 'BIH', 70, 44f),
    BW(Continent.AF, 'Botswana', 'BW', 'BWA', 72, -22f),
    BV(Continent.AN, 'Bouvet Island', 'BV', 'BVT', 74, -54.4333f),
    BR(Continent.SA, 'Brazil', 'BR', 'BRA', 76, -10f),
    IO(Continent.AS, 'British Indian Ocean Territory', 'IO', 'IOT', 86, -6f),
    BN(Continent.AS, 'Brunei', 'BN', 'BRN', 96, 4.5f),
    BG(Continent.EU, 'Bulgaria', 'BG', 'BGR', 100, 43f),
    BF(Continent.AF, 'Burkina Faso', 'BF', 'BFA', 854, 13f),
    BI(Continent.AF, 'Burundi', 'BI', 'BDI', 108, -3.5f),
    KH(Continent.AS, 'Cambodia', 'KH', 'KHM', 116, 13f),
    CM(Continent.AF, 'Cameroon', 'CM', 'CMR', 120, 6f),
    CA(Continent.NA, 'Canada', 'CA', 'CAN', 124, 60f),
    CV(Continent.AF, 'Cape Verde', 'CV', 'CPV', 132, 16f),
    CS(Continent.EU, 'Czechoslovakia', 'CS', 'CSK', 132, 16f),
    KY(Continent.NA, 'Cayman Islands', 'KY', 'CYM', 136, 19.5f),
    CF(Continent.AF, 'Central African Republic', 'CF', 'CAF', 140, 7f),
    TD(Continent.AF, 'Chad', 'TD', 'TCD', 148, 15f),
    CL(Continent.SA, 'Chile', 'CL', 'CHL', 152, -30f),
    CN(Continent.AS, 'China', 'CN', 'CHN', 156, 35f),
    CX(Continent.AS, 'Christmas Island', 'CX', 'CXR', 162, -10.5f),
    CC(Continent.AS, 'Cocos (Keeling) Islands', 'CC', 'CCK', 166, -12.5f),
    CO(Continent.SA, 'Colombia', 'CO', 'COL', 170, 4f),
    CW(Continent.NA, 'Curaçao', 'CW', 'CUW', 170, 4f),
    KM(Continent.AF, 'Comoros', 'KM', 'COM', 174, -12.1667f),
    CG(Continent.AF, 'Congo', 'CG', 'COG', 178, -1f),
    CD(Continent.AF, 'Congo, the Democratic Republic of the', 'CD', 'COD', 180, 0f),
    CK(Continent.OC, 'Cook Islands', 'CK', 'COK', 184, -21.2333f),
    CR(Continent.NA, 'Costa Rica', 'CR', 'CRI', 188, 10f),
    CI(Continent.AF, 'Ivory Coast', 'CI', 'CIV', 384, 8f),
    HR(Continent.EU, 'Croatia', 'HR', 'HRV', 191, 45.1667f),
    CU(Continent.NA, 'Cuba', 'CU', 'CUB', 192, 21.5f),
    CY(Continent.EU, 'Cyprus', 'CY', 'CYP', 196, 35f),
    CZ(Continent.EU, 'Czech Republic', 'CZ', 'CZE', 203, 49.75f),
    DK(Continent.EU, 'Denmark', 'DK', 'DNK', 208, 56f),
    DJ(Continent.AF, 'Djibouti', 'DJ', 'DJI', 262, 11.5f),
    DM(Continent.NA, 'Dominica', 'DM', 'DMA', 212, 15.4167f),
    DO(Continent.NA, 'Dominican Republic', 'DO', 'DOM', 214, 19f),
    EC(Continent.SA, 'Ecuador', 'EC', 'ECU', 218, -2f),
    EG(Continent.AF, 'Egypt', 'EG', 'EGY', 818, 27f),
    SV(Continent.NA, 'El Salvador', 'SV', 'SLV', 222, 13.8333f),
    GQ(Continent.AF, 'Equatorial Guinea', 'GQ', 'GNQ', 226, 2f),
    ER(Continent.AF, 'Eritrea', 'ER', 'ERI', 232, 15f),
    EE(Continent.EU, 'Estonia', 'EE', 'EST', 233, 59f),
    ET(Continent.AF, 'Ethiopia', 'ET', 'ETH', 231, 8f),
    FK(Continent.SA, 'Falkland Islands (Malvinas)', 'FK', 'FLK', 238, -51.75f),
    FO(Continent.EU, 'Faroe Islands', 'FO', 'FRO', 234, 62f),
    FJ(Continent.OC, 'Fiji', 'FJ', 'FJI', 242, -18f),
    FI(Continent.EU, 'Finland', 'FI', 'FIN', 246, 64f),
    FL(Continent.EU, 'Liechtenstein', 'FL', 'LIE', 47, 9.55f),
    FR(Continent.EU, 'France', 'FR', 'FRA', 250, 46f),
    GF(Continent.SA, 'French Guiana', 'GF', 'GUF', 254, 4f),
    PF(Continent.OC, 'French Polynesia', 'PF', 'PYF', 258, -15f),
    TF(Continent.AN, 'French Southern Territories', 'TF', 'ATF', 260, -43f),
    GA(Continent.AF, 'Gabon', 'GA', 'GAB', 266, -1f),
    GM(Continent.AF, 'Gambia', 'GM', 'GMB', 270, 13.4667f),
    GE(Continent.EU, 'Georgia', 'GE', 'GEO', 268, 42f),
    DE(Continent.EU, 'Germany', 'DE', 'DEU', 276, 51f),
    GH(Continent.AF, 'Ghana', 'GH', 'GHA', 288, 8f),
    GI(Continent.EU, 'Gibraltar', 'GI', 'GIB', 292, 36.1833f),
    GR(Continent.EU, 'Greece', 'GR', 'GRC', 300, 39f),
    GL(Continent.NA, 'Greenland', 'GL', 'GRL', 304, 72f),
    GD(Continent.NA, 'Grenada', 'GD', 'GRD', 308, 12.1167f),
    GP(Continent.NA, 'Guadeloupe', 'GP', 'GLP', 312, 16.25f),
    GU(Continent.OC, 'Guam', 'GU', 'GUM', 316, 13.4667f),
    GT(Continent.NA, 'Guatemala', 'GT', 'GTM', 320, 15.5f),
    GG(Continent.EU, 'Guernsey', 'GG', 'GGY', 831, 49.4667f),
    GN(Continent.AF, 'Guinea', 'GN', 'GIN', 324, 11f),
    GW(Continent.AF, 'Guinea-Bissau', 'GW', 'GNB', 624, 12f),
    GY(Continent.SA, 'Guyana', 'GY', 'GUY', 328, 5f),
    HT(Continent.NA, 'Haiti', 'HT', 'HTI', 332, 19f),
    HM(Continent.AN, 'Heard Island and McDonald Islands', 'HM', 'HMD', 334, -53.1f),
    VA(Continent.EU, 'Vatican City State', 'VA', 'VAT', 336, 41.9f),
    HN(Continent.NA, 'Honduras', 'HN', 'HND', 340, 15f),
    HK(Continent.AS, 'Hong Kong', 'HK', 'HKG', 344, 22.25f),
    HU(Continent.EU, 'Hungary', 'HU', 'HUN', 348, 47f),
    IS(Continent.EU, 'Iceland', 'IS', 'ISL', 352, 65f),
    IN(Continent.AS, 'India', 'IN', 'IND', 356, 20f),
    ID(Continent.AS, 'Indonesia', 'ID', 'IDN', 360, -5f),
    IR(Continent.AS, 'Iran', 'IR', 'IRN', 364, 32f),
    IQ(Continent.AS, 'Iraq', 'IQ', 'IRQ', 368, 33f),
    IE(Continent.EU, 'Ireland', 'IE', 'IRL', 372, 53f),
    IM(Continent.EU, 'Isle of Man', 'IM', 'IMN', 833, 54.23f),
    IL(Continent.AS, 'Israel', 'IL', 'ISR', 376, 31.5f),
    IT(Continent.EU, 'Italy', 'IT', 'ITA', 380, 42.8333f),
    JM(Continent.NA, 'Jamaica', 'JM', 'JAM', 388, 18.25f),
    JP(Continent.AS, 'Japan', 'JP', 'JPN', 392, 36f),
    JE(Continent.EU, 'Jersey', 'JE', 'JEY', 832, 49.21f),
    JO(Continent.AS, 'Jordan', 'JO', 'JOR', 400, 31f),
    KZ(Continent.AS, 'Kazakhstan', 'KZ', 'KAZ', 398, 48f),
    KE(Continent.AF, 'Kenya', 'KE', 'KEN', 404, 1f),
    KI(Continent.OC, 'Kiribati', 'KI', 'KIR', 296, 1.4167f),
    KP(Continent.AS, "Korea, Democratic People's Republic of", 'KP', 'PRK', 408, 40f),
    KR(Continent.AS, 'Korea, Republic of', 'KR', 'KOR', 410, 37f),
    KW(Continent.AS, 'Kuwait', 'KW', 'KWT', 414, 29.3375f),
    KG(Continent.AS, 'Kyrgyzstan', 'KG', 'KGZ', 417, 41f),
    LA(Continent.AS, "Lao People's Democratic Republic", 'LA', 'LAO', 418, 18f),
    LV(Continent.EU, 'Latvia', 'LV', 'LVA', 428, 57f),
    LB(Continent.AS, 'Lebanon', 'LB', 'LBN', 422, 33.8333f),
    LS(Continent.AF, 'Lesotho', 'LS', 'LSO', 426, -29.5f),
    LR(Continent.AF, 'Liberia', 'LR', 'LBR', 430, 6.5f),
    LY(Continent.AF, 'Libyan Arab Jamahiriya', 'LY', 'LBY', 434, 25f),
    LI(Continent.EU, 'Liechtenstein', 'LI', 'LIE', 438, 47.1667f),
    LT(Continent.EU, 'Lithuania', 'LT', 'LTU', 440, 56f),
    LU(Continent.EU, 'Luxembourg', 'LU', 'LUX', 442, 49.75f),
    MO(Continent.AS, 'Macao', 'MO', 'MAC', 446, 22.1667f),
    MK(Continent.EU, 'North Macedonia', 'MK', 'MKD', 807, 41.8333f),
    MG(Continent.AF, 'Madagascar', 'MG', 'MDG', 450, -20f),
    MW(Continent.AF, 'Malawi', 'MW', 'MWI', 454, -13.5f),
    MY(Continent.AS, 'Malaysia', 'MY', 'MYS', 458, 2.5f),
    MV(Continent.AS, 'Maldives', 'MV', 'MDV', 462, 3.25f),
    ML(Continent.AF, 'Mali', 'ML', 'MLI', 466, 17f),
    MT(Continent.EU, 'Malta', 'MT', 'MLT', 470, 35.8333f),
    MH(Continent.OC, 'Marshall Islands', 'MH', 'MHL', 584, 9f),
    MQ(Continent.NA, 'Martinique', 'MQ', 'MTQ', 474, 14.6667f),
    MR(Continent.AF, 'Mauritania', 'MR', 'MRT', 478, 20f),
    MU(Continent.AF, 'Mauritius', 'MU', 'MUS', 480, -20.2833f),
    YT(Continent.AF, 'Mayotte', 'YT', 'MYT', 175, -12.8333f),
    MX(Continent.NA, 'Mexico', 'MX', 'MEX', 484, 23f),
    FM(Continent.OC, 'Micronesia, Federated States of', 'FM', 'FSM', 583, 6.9167f),
    MD(Continent.EU, 'Moldova, Republic of', 'MD', 'MDA', 498, 47f),
    MC(Continent.EU, 'Monaco', 'MC', 'MCO', 492, 43.7333f),
    MN(Continent.AS, 'Mongolia', 'MN', 'MNG', 496, 46f),
    ME(Continent.EU, 'Montenegro', 'ME', 'MNE', 499, 42.5f),
    MS(Continent.NA, 'Montserrat', 'MS', 'MSR', 500, 16.75f),
    MA(Continent.AF, 'Morocco', 'MA', 'MAR', 504, 32f),
    MZ(Continent.AF, 'Mozambique', 'MZ', 'MOZ', 508, -18.25f),
    MM(Continent.AS, 'Myanmar', 'MM', 'MMR', 104, 22f),
    NA(Continent.AF, 'Namibia', 'NA', 'NAM', 516, -22f),
    NR(Continent.OC, 'Nauru', 'NR', 'NRU', 520, -0.5333f),
    NP(Continent.AS, 'Nepal', 'NP', 'NPL', 524, 28f),
    NL(Continent.EU, 'Netherlands', 'NL', 'NLD', 528, 52.5f),
    AN(Continent.NA, 'Netherlands Antilles', 'AN', 'ANT', 530, 12.25f),
    NC(Continent.OC, 'New Caledonia', 'NC', 'NCL', 540, -21.5f),
    NZ(Continent.OC, 'New Zealand', 'NZ', 'NZL', 554, -41f),
    NI(Continent.NA, 'Nicaragua', 'NI', 'NIC', 558, 13f),
    NE(Continent.AF, 'Niger', 'NE', 'NER', 562, 16f),
    NG(Continent.AF, 'Nigeria', 'NG', 'NGA', 566, 10f),
    NU(Continent.OC, 'Niue', 'NU', 'NIU', 570, -19.0333f),
    NF(Continent.OC, 'Norfolk Island', 'NF', 'NFK', 574, -29.0333f),
    MP(Continent.OC, 'Northern Mariana Islands', 'MP', 'MNP', 580, 15.2f),
    NO(Continent.EU, 'Norway', 'NO', 'NOR', 578, 62f),
    OM(Continent.AS, 'Oman', 'OM', 'OMN', 512, 21f),
    PK(Continent.AS, 'Pakistan', 'PK', 'PAK', 586, 30f),
    PW(Continent.OC, 'Palau', 'PW', 'PLW', 585, 7.5f),
    PS(Continent.AS, 'Palestine, State of', 'PS', 'PSE', 275, 31.9f),
    PA(Continent.NA, 'Panama', 'PA', 'PAN', 591, 9f),
    PG(Continent.OC, 'Papua New Guinea', 'PG', 'PNG', 598, -6f),
    PY(Continent.SA, 'Paraguay', 'PY', 'PRY', 600, -23f),
    PE(Continent.SA, 'Peru', 'PE', 'PER', 604, -10f),
    PH(Continent.AS, 'Philippines', 'PH', 'PHL', 608, 13f),
    PN(Continent.OC, 'Pitcairn', 'PN', 'PCN', 612, -25.0667f),
    PL(Continent.EU, 'Poland', 'PL', 'POL', 616, 52f),
    PT(Continent.EU, 'Portugal', 'PT', 'PRT', 620, 39.5f),
    PR(Continent.NA, 'Puerto Rico', 'PR', 'PRI', 630, 18.25f),
    QA(Continent.AS, 'Qatar', 'QA', 'QAT', 634, 25.5f),
    RE(Continent.AF, 'Reunion', 'RE', 'REU', 638, -21.1f),
    RO(Continent.EU, 'Romania', 'RO', 'ROU', 642, 46f),
    RU(Continent.EU, 'Russian Federation', 'RU', 'RUS', 643, 60f),
    RW(Continent.AF, 'Rwanda', 'RW', 'RWA', 646, -2f),
    BL(Continent.NA, 'Saint Barthelemy', 'BL', 'BLM', 652, 18.5f),
    SH(Continent.AF, 'Saint Helena', 'SH', 'SHN', 654, -15.9333f),
    KN(Continent.NA, 'Saint Kitts and Nevis', 'KN', 'KNA', 659, 17.3333f),
    LC(Continent.NA, 'Saint Lucia', 'LC', 'LCA', 662, 13.8833f),
    MF(Continent.NA, 'Saint Martin (French part)', 'MF', 'MAF', 663, 18.0833f),
    PM(Continent.NA, 'Saint Pierre and Miquelon', 'PM', 'SPM', 666, 46.8333f),
    VC(Continent.NA, 'Saint Vincent and the Grenadines', 'VC', 'VCT', 670, 13.25f),
    WS(Continent.OC, 'Samoa', 'WS', 'WSM', 882, -13.5833f),
    SM(Continent.EU, 'San Marino', 'SM', 'SMR', 674, 43.7667f),
    ST(Continent.AF, 'Sao Tome and Principe', 'ST', 'STP', 678, 1f),
    SA(Continent.AS, 'Saudi Arabia', 'SA', 'SAU', 682, 25f),
    SN(Continent.AF, 'Senegal', 'SN', 'SEN', 686, 14f),
    RS(Continent.EU, 'Serbia', 'RS', 'SRB', 688, 44f),
    SC(Continent.AF, 'Seychelles', 'SC', 'SYC', 690, -4.5833f),
    SL(Continent.AF, 'Sierra Leone', 'SL', 'SLE', 694, 8.5f),
    SG(Continent.AS, 'Singapore', 'SG', 'SGP', 702, 1.3667f),
    SK(Continent.EU, 'Slovakia', 'SK', 'SVK', 703, 48.6667f),
    SI(Continent.EU, 'Slovenia', 'SI', 'SVN', 705, 46f),
    SB(Continent.OC, 'Solomon Islands', 'SB', 'SLB', 90, -8f),
    SO(Continent.AF, 'Somalia', 'SO', 'SOM', 706, 10f),
    ZA(Continent.AF, 'South Africa', 'ZA', 'ZAF', 710, -29f),
    GS(Continent.AN, 'South Georgia and the South Sandwich Islands', 'GS', 'SGS', 239, -54.5f),
    SS(Continent.AF, 'South Sudan', 'SS', 'SSD', 728, 7f),
    ES(Continent.EU, 'Spain', 'ES', 'ESP', 724, 40f),
    LK(Continent.AS, 'Sri Lanka', 'LK', 'LKA', 144, 7f),
    SD(Continent.AF, 'Sudan', 'SD', 'SDN', 729, 15f),
    SR(Continent.SA, 'Suriname', 'SR', 'SUR', 740, 4f),
    SJ(Continent.EU, 'Svalbard and Jan Mayen', 'SJ', 'SJM', 744, 78f),
    SZ(Continent.AF, 'Swaziland', 'SZ', 'SWZ', 748, -26.5f),
    SE(Continent.EU, 'Sweden', 'SE', 'SWE', 752, 62f),
    CH(Continent.EU, 'Switzerland', 'CH', 'CHE', 756, 47f),
    SY(Continent.AS, 'Syrian Arab Republic', 'SY', 'SYR', 760, 35f),
    TW(Continent.AS, 'Taiwan', 'TW', 'TWN', 158, 23.5f),
    TJ(Continent.AS, 'Tajikistan', 'TJ', 'TJK', 762, 39f),
    TZ(Continent.AF, 'Tanzania, United Republic of', 'TZ', 'TZA', 834, -6f),
    TH(Continent.AS, 'Thailand', 'TH', 'THA', 764, 15f),
    TL(Continent.AS, 'Timor-Leste', 'TL', 'TLS', 626, -8.55f),
    TG(Continent.AF, 'Togo', 'TG', 'TGO', 768, 8f),
    TK(Continent.OC, 'Tokelau', 'TK', 'TKL', 772, -9f),
    TO(Continent.OC, 'Tonga', 'TO', 'TON', 776, -20f),
    TT(Continent.NA, 'Trinidad and Tobago', 'TT', 'TTO', 780, 11f),
    TN(Continent.AF, 'Tunisia', 'TN', 'TUN', 788, 34f),
    TR(Continent.AS, 'Turkey', 'TR', 'TUR', 792, 39f),
    TM(Continent.AS, 'Turkmenistan', 'TM', 'TKM', 795, 40f),
    TC(Continent.NA, 'Turks and Caicos Islands', 'TC', 'TCA', 796, 21.75f),
    TV(Continent.OC, 'Tuvalu', 'TV', 'TUV', 798, -8f),
    UG(Continent.AF, 'Uganda', 'UG', 'UGA', 800, 1f),
    UA(Continent.EU, 'Ukraine', 'UA', 'UKR', 804, 49f),
    AE(Continent.AS, 'United Arab Emirates', 'AE', 'ARE', 784, 24f),
    GB(Continent.EU, 'United Kingdom', 'GB', 'GBR', 826, 54f),
    US(Continent.NA, 'United States', 'US', 'USA', 840, 38f),
    UM(Continent.OC, 'United States Minor Outlying Islands', 'UM', 'UMI', 581, 19.2833f),
    UY(Continent.SA, 'Uruguay', 'UY', 'URY', 858, -33f),
    UZ(Continent.AS, 'Uzbekistan', 'UZ', 'UZB', 860, 41f),
    VU(Continent.OC, 'Vanuatu', 'VU', 'VUT', 548, -16f),
    VE(Continent.SA, 'Venezuela', 'VE', 'VEN', 862, 8f),
    VN(Continent.AS, 'Viet Nam', 'VN', 'VNM', 704, 16f),
    VG(Continent.NA, 'Virgin Islands, British', 'VG', 'VGB', 92, 18.5f),
    VI(Continent.NA, 'Virgin Islands, U.S.', 'VI', 'VIR', 850, 18.3333f),
    WF(Continent.OC, 'Wallis and Futuna', 'WF', 'WLF', 876, -13.3f),
    EH(Continent.AF, 'Western Sahara', 'EH', 'ESH', 732, 24.5f),
    YE(Continent.AS, 'Yemen', 'YE', 'YEM', 887, 15f),
    ZM(Continent.AF, 'Zambia', 'ZM', 'ZMB', 894, -15f),
    ZW(Continent.AF, 'Zimbabwe', 'ZW', 'ZWE', 716, -20f),
    AX(Continent.EU, 'Aruba', 'AW', 'ABW', 533, 12.5f),
    BQ(Continent.NA, 'Caribbean Netherlands', 'BQ', 'BES', 68, -17f),
    SX(Continent.NA, 'Sint Maarten', 'SX', 'SXM', 99, 0.0f),
    XK(Continent.EU, 'Kosovo', 'XK', 'XKX', 43, 20.9f)

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

    String nameForLanguage(String lang = 'en') {
        def locale = new Locale('', this.alphaIso2)
        return locale.getDisplayCountry(new Locale(lang, this.alphaIso2))
    }

    static IEnumOption[] getEnumOptions(Country... firsts = null) {
        IEnumOption[] res = new IEnumOption[values().size() + Continent.values().size()]
        int i = 0

        Continent.values().sort {it.name }.each { c ->
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
            values().findAll {it.continent == c }.sort { it.name }.each {
                res[i++] = new IEnumOption() {
                    @Override
                    String getKey() {
                        return it.alphaIso2
                    }

                    @Override
                    String getValue() {
                        return it.name
                    }

                    @Override
                    String getAsset() {
                        return "taack/icons/countries/4x3/${it.alphaIso2.toLowerCase()}.webp"
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