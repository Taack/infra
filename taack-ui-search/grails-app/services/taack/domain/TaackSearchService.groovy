package taack.domain

import grails.compiler.GrailsCompileStatic
import grails.util.Pair
import grails.web.api.WebAttributes
import org.codehaus.groovy.runtime.MethodClosure as MC
import org.grails.datastore.gorm.GormEntity
import taack.solr.SolrSpecifier
import taack.ui.dsl.UiBlockSpecifier
/**
 * Full text search service support.
 *
 * <p>To add domain to the search, in a service:
 * <pre>{@code
 * @GrailsCompileStatic
 *  class CrewSearchService implements TaackSearchService.IIndexService {
 *
 *      static lazyInit = false
 *      TaackSearchService taackSearchService
 *
 * @PostConstruct
 *      private void init() {
 *          taackSearchService.registerSolrSpecifier(this, new SolrSpecifier(User, CrewController.&showUserFromSearch as MC, this.&labeling as MC, { User u ->
 *              u ?= new User()
 *              indexField "User Name (without Accents)", SolrFieldType.TXT_NO_ACCENT, u.username_
 *              indexField "User Name", SolrFieldType.TXT_GENERAL, u.username_
 *              indexField "First Name", SolrFieldType.TXT_NO_ACCENT, u.firstName_
 *              indexField "Last Name", SolrFieldType.TXT_NO_ACCENT, u.lastName_
 *              indexField "Subsidiary", SolrFieldType.POINT_STRING, "mainSubsidiary", true, u.mainSubsidiary?.toString()
 *              indexField "Business Unit", SolrFieldType.POINT_STRING, "businessUnit", true, u.businessUnit?.toString()
 *              indexField "Date Created", SolrFieldType.DATE, 0.5f, true, u.dateCreated_
 *              indexField "User Created", SolrFieldType.POINT_STRING, "userCreated", 0.5f, true, u.userCreated?.username
 *          }))
 *      }
 *
 * @Override
 *      List<? extends GormEntity> indexThose(Class<? extends GormEntity> toIndex) {
 *          if (toIndex.isAssignableFrom(Ticket2Issue)) return Ticket2Issue.findAllByActive(true)
 *          else null
 *      }
 * }</pre>
 *
 */
@GrailsCompileStatic
final class TaackSearchService implements WebAttributes {

    TaackSolrSearchService taackSolrSearchService

    /**
     * Service interface allowing an application module to index its domain.
     *
     * Returned entities will be indexed as specified via {@link #registerSolrSpecifier(taack.domain.TaackSearchService.IIndexService, taack.solr.SolrSpecifier)}
     */
    interface IIndexService {
        List<? extends GormEntity> indexThose(Class<? extends GormEntity> toIndex)
    }

    private Map<Class<? extends GormEntity>, Pair<IIndexService, SolrSpecifier>> mapSolrSpecifier = [:]

    /**
     * Register the way each domain class will be indexed and faceted.
     *
     * @param indexService The service
     * @param solrSpecifier How to index data
     */
    final void registerSolrSpecifier(IIndexService indexService, SolrSpecifier solrSpecifier) {
        mapSolrSpecifier.putIfAbsent(solrSpecifier.type, new Pair(indexService, solrSpecifier))
    }

    /**
     * Build search block
     *
     * @param q query
     * @param search Action that will display the block (that will call this method)
     * @param classes registered class
     * @return block with search results
     */
    final UiBlockSpecifier search(String q, MC search, Class<? extends GormEntity>... classes) {
        taackSolrSearchService.search(q, search, mapSolrSpecifier, classes)
    }

    final void indexAll() {
        taackSolrSearchService.indexAll(mapSolrSpecifier)
    }

    final void indexAllOnly(String name) {
        taackSolrSearchService.indexAll(mapSolrSpecifier)
    }

    String fileContentToStringWithoutOcr(InputStream stream) {
        taackSolrSearchService.fileContentToStringWithoutOcr(stream)
    }

    String fileContentToStringWithOcr(InputStream stream) {
        taackSolrSearchService.fileContentToStringWithOcr(stream)
    }

}
