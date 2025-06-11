package crew


import grails.compiler.GrailsCompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.domain.TaackGormClass
import taack.domain.TaackGormClassRegisterService
import taack.domain.TaackSearchService
import taack.solr.SolrSpecifier
import taack.solr.SolrFieldType
import taack.ui.dsl.UiBlockSpecifier

import javax.annotation.PostConstruct

@GrailsCompileStatic
class CrewSearchService implements TaackSearchService.IIndexService {

    static lazyInit = false

    TaackSearchService taackSearchService

    @PostConstruct
    private void init() {
        taackSearchService.registerSolrSpecifier(this, new SolrSpecifier(User, { User u ->
            u ?= new User()
            indexField SolrFieldType.TXT_NO_ACCENT, u.username_
            indexField SolrFieldType.TXT_GENERAL, u.username_
            indexField SolrFieldType.TXT_NO_ACCENT, u.firstName_
            indexField SolrFieldType.TXT_NO_ACCENT, u.lastName_
            indexField SolrFieldType.POINT_STRING, "mainSubsidiary", true, u.subsidiary?.toString()
            indexField SolrFieldType.POINT_STRING, "businessUnit", true, u.businessUnit?.toString()
            indexField SolrFieldType.DATE, 0.5f, true, u.dateCreated_
            indexField SolrFieldType.POINT_STRING, "userCreated", 0.5f, true, u.userCreated?.username
        }))
        TaackGormClassRegisterService.register(
                new TaackGormClass(User.class).builder
                        .setShowMethod(CrewController.&showUserFromSearch as MethodClosure)
                        .setShowLabel({ Long id ->
                            def u = User.read(id)
                            return "User: ${u.firstName} ${u.lastName} ($id)"
                        }).build()
        )
    }

    @Override
    List<? extends GormEntity> indexThose(Class<? extends GormEntity> toIndex) {
        if (toIndex.isAssignableFrom(User)) return User.findAllByEnabled(true)
        else null
    }

    UiBlockSpecifier buildSearchBlock(String q) {
        taackSearchService.search(q, CrewController.&search as MethodClosure, User)
    }
}
