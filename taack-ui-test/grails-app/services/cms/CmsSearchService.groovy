package cms

import crew.config.SupportedLanguage
import grails.compiler.GrailsCompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.domain.TaackSearchService
import taack.solr.SolrFieldType
import taack.solr.SolrSpecifier
import taack.ui.dsl.UiBlockSpecifier

import jakarta.annotation.PostConstruct

@GrailsCompileStatic
class CmsSearchService implements TaackSearchService.IIndexService {

    static lazyInit = false

    TaackSearchService taackSearchService

    @PostConstruct
    void init() {
        TaackSearchService.registerSolrSpecifier(this, new SolrSpecifier(CmsPage, CmsController.&editPage as MethodClosure, this.&labelingPage as MethodClosure, { CmsPage p ->
            p ?= new CmsPage()
            if (p.id)
                for (SupportedLanguage l in SupportedLanguage.values()) {
                    if (p.title[l.toString().toLowerCase()]) {
                        indexField SolrFieldType.TXT_NO_ACCENT, "pageTitle-${l.toString().toLowerCase()}", p.title[l.toString().toLowerCase()]
                        indexField SolrFieldType.TXT_GENERAL, "pageTitle-${l.toString().toLowerCase()}", p.title[l.toString().toLowerCase()]
                    }
                    if (p.bodyContent[l.toString().toLowerCase()]) {
                        indexField SolrFieldType.TXT_NO_ACCENT, "pageBody-${l.toString().toLowerCase()}", p.bodyContent[l.toString().toLowerCase()]
                        indexField SolrFieldType.TXT_GENERAL, "pageBody-${l.toString().toLowerCase()}", p.bodyContent[l.toString().toLowerCase()]
                    }
                }
            else {
                def cl = SupportedLanguage.fromContext()
                indexField SolrFieldType.TXT_NO_ACCENT, "pageTitle-${cl.toString().toLowerCase()}', 'p.title[l.iso2]"
                indexField SolrFieldType.TXT_GENERAL, "pageTitle-${cl.toString().toLowerCase()}', 'p.title[l.iso2]"
                indexField SolrFieldType.TXT_NO_ACCENT, "pageBody-${cl.toString().toLowerCase()}', 'p.bodyContent[l.iso2]"
                indexField SolrFieldType.TXT_GENERAL, "pageBody-${cl.toString().toLowerCase()}', 'p.bodyContent[l.iso2]"

            }
            indexField SolrFieldType.POINT_STRING, 'subsidiary', true, p.subsidiary?.toString()
            indexField SolrFieldType.DATE, 0.5f, true, p.dateCreated_
            indexField SolrFieldType.POINT_STRING, 'userCreated', 0.5f, true, p.userCreated?.username
            indexField SolrFieldType.DATE, 0.5f, true, p.lastUpdated_
            indexField SolrFieldType.POINT_STRING, 'userUpdated', 0.5f, true, p.userUpdated?.username
        }))
    }

    String labelingPage(Long id) {
        def i = CmsPage.read(id)
        "Page: ${i.name} ${i.name ?: ''} ($id)"
    }

    @Override
    List<? extends GormEntity> indexThose(Class<? extends GormEntity> toIndex) {
        if (toIndex.isAssignableFrom(CmsPage)) return CmsPage.findAllByBodyContentIsNotNull()
        else null
    }

    UiBlockSpecifier buildSearchBlock(String q) {
        taackSearchService.search(q, CmsController.&search as MethodClosure, CmsPage)
    }

}
