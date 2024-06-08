package cms.config


import crew.config.Subsidiary
import crew.config.SupportedLanguage
import groovy.transform.CompileStatic

@CompileStatic
enum CmsSubsidiary {
    BRAND1(Subsidiary.YOUR_SUBSIDIARY1, 'brand1', SupportedLanguage.FR, SupportedLanguage.EN),
    BRAND2(Subsidiary.YOUR_SUBSIDIARY2, 'brand1', SupportedLanguage.DE, SupportedLanguage.EN)

    CmsSubsidiary(Subsidiary subsidiary, String brandName, SupportedLanguage... languages) {
        this.subsidiary = subsidiary
        this.languages = languages
        this.brandName = brandName
    }

    final Subsidiary subsidiary
    final String brandName
    final SupportedLanguage[] languages

    static CmsSubsidiary from(Subsidiary subsidiary) {
        values().find { it.subsidiary == subsidiary }
    }

    static CmsSubsidiary fromBrand(String brand) {
        values().find { it.brandName == brand }
    }
}

@CompileStatic
enum CmsServer {
    // CITEL_INC('citel.us', CmsSubsidiary.CITEL_INC),
    BRAND1_TESTING('testbrand1', CmsSubsidiary.BRAND1),
    BRAND2_TESTING('testbrand2', CmsSubsidiary.BRAND2)

    CmsServer(String url, CmsSubsidiary... subsidiaries) {
        this.url = url
        this.subsidiaries = subsidiaries
    }

    final String url
    final CmsSubsidiary[] subsidiaries
}
