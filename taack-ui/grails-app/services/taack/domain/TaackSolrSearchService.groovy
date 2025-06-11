package taack.domain

import grails.compiler.GrailsCompileStatic
import grails.util.Pair
import grails.web.api.WebAttributes
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrRequest
import org.apache.solr.client.solrj.impl.Http2SolrClient
import org.apache.solr.client.solrj.request.schema.AnalyzerDefinition
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition
import org.apache.solr.client.solrj.request.schema.SchemaRequest
import org.apache.solr.client.solrj.response.FacetField
import org.apache.solr.client.solrj.response.GroupResponse
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.response.RangeFacet
import org.apache.solr.common.SolrInputDocument
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.parser.ocr.TesseractOCRConfig
import org.apache.tika.sax.BodyContentHandler
import org.codehaus.groovy.runtime.MethodClosure as MC
import org.grails.datastore.gorm.GormEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.web.servlet.support.RequestContextUtils as RCU
import taack.solr.SolrIndexerVisitor
import taack.solr.SolrSearcherVisitor
import taack.solr.SolrSpecifier
import taack.ui.TaackUiConfiguration
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.IconStyle

import javax.annotation.PostConstruct


@GrailsCompileStatic
final class TaackSolrSearchService implements WebAttributes {

    @Autowired
    TaackUiConfiguration taackUiConfiguration

    @Autowired
    MessageSource messageSource

    private SolrClient solrClient

    @PostConstruct
    void init() {
        solrClient = new Http2SolrClient.Builder(taackUiConfiguration.solrUrl).build()
    }

    final UiBlockSpecifier search(String q, MC search, Map<Class<? extends GormEntity>, Pair<TaackSearchService.IIndexService, SolrSpecifier>> mapSolrSpecifier, Class<? extends GormEntity>... classes) {
        List<String> facetsClicked = params.list("facetsClicked")
        List<String> rangesClicked = params.list("rangesClicked")
        SolrQuery sq = new SolrQuery(q)
        sq.add("defType", "edismax")
//        sq.add("facet.limit", "-1")
        sq.add("hl.snippets", "4")
        sq.add("hl.mergeContiguous", "true")
        sq.add("hl.highlightMultiTerm", "true")
        sq.add("facet.mincount", "1")
        //sq.add("facet.range", "lastUpdated_dt")
        //sq.add("facet.range", "lastUpdated_dt")
        sq.add("facet.range", "dateCreated_dt")
        sq.add("facet.range", "lastUpdated_dt")
        sq.add("facet.range.start", "NOW/MONTHS-10YEARS")
        sq.add("facet.range.end", "NOW/MONTHS")
        sq.add("facet.range.gap", "+12MONTHS")
        sq.add("f.lastUpdated_dt.facet.range.start", "NOW/MONTHS-10YEARS")
        sq.add("f.lastUpdated_dt.facet.range.end", "NOW/MONTHS")
        sq.add("f.lastUpdated_dt.facet.range.gap", "+12MONTHS")
        sq.add("f.dateCreated_dt.facet.range.start", "NOW/MONTHS-10YEARS")
        sq.add("f.dateCreated_dt.facet.range.end", "NOW/MONTHS")
        sq.add("f.dateCreated_dt.facet.range.gap", "+12MONTHS")
        sq.add("ps", "7")
        sq.add("group", "false")
        sq.rows = 70
        //sq.add("group.field", "type_s")
        //sq.add("group.limit", "10")

        for (String f in facetsClicked) {
            sq.add("fq", f.replace(";", ":"))
        }
        for (String r in rangesClicked) {
            sq.add("fq", r.replace(";", ":"))
        }

        def targetClasses = (classes ?: mapSolrSpecifier.keySet()) as Collection<Class<? extends GormEntity>>
        sq.addFacetField("type_s")
        Map<String, String> i18nMap = [:]
        Map<String, Class> classMap = [:]
        Set<String> highlightFields = []
        Set<String> queryFields = []
        String fqType = "type_s:${targetClasses*.simpleName.join(' OR type_s:')}"
        sq.add("fq", fqType)
        for (def c in targetClasses) {
            def s = mapSolrSpecifier[c]?.bValue
            def visitor = new SolrSearcherVisitor(RCU.getLocale(webRequest.request), messageSource)
            s.visitSolr(visitor)
            for (def f in visitor.facets) {
                sq.addFacetField(f)
            }
            i18nMap.putAll visitor.i18nMap
            classMap.put c.simpleName, c
            highlightFields.addAll visitor.fields
            queryFields.addAll visitor.boostFields
        }
        sq.addHighlightField(highlightFields.join(' '))
        sq.add("qf", queryFields.join(' '))
        QueryResponse queryResponse = solrClient.query(sq, SolrRequest.METHOD.POST)
        GroupResponse groupResponse = queryResponse.groupResponse
        List<FacetField> facets = queryResponse.facetFields
        List<RangeFacet> ranges = queryResponse.facetRanges
        Map<String, Map<String, List<String>>> highlighting = queryResponse.highlighting
        def response = queryResponse.response

        new UiBlockSpecifier().ui {
            row {
                col BlockSpec.Width.THIRD, {
                    ajaxBlock "range", {
                        table new UiTableSpecifier().ui {
                            for (def r in ranges) {
                                if (i18nMap[r.name]) {
                                    header {
                                        column {
                                            label(i18nMap[r.name] ?: 'Type')
                                        }
                                    }
                                    for (def c in r.counts) {
                                        String currentRange = "${r.name};[${c.value} TO ${c.value}+6MONTHS]"
                                        row {
                                            rowColumn() {
                                                if (rangesClicked.contains(currentRange)) rowAction ActionIcon.DELETE * IconStyle.SCALE_DOWN, search as MC, [rangesClicked: rangesClicked - [currentRange], facetsClicked: facetsClicked, q: q]
                                                else rowAction ActionIcon.FILTER * IconStyle.SCALE_DOWN, search, [rangesClicked: rangesClicked + [currentRange], facetsClicked: facetsClicked, q: q] as Map<String, ?>
                                                rowField c.value + "(${c.count})"
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ajaxBlock "faceting", {
                        table new UiTableSpecifier().ui {
                            for (def f in facets) {
                                header {
                                    column(2) {
                                        label(i18nMap[f.name] ?: 'Type')
                                    }
                                }
                                for (def v in f.values) {
                                    String currentFacet = "${f.name ?: "type_s"};${v.name}"
                                    row {
                                        rowColumn {
                                            if (facetsClicked.contains(currentFacet)) rowAction ActionIcon.DELETE * IconStyle.SCALE_DOWN, search, [facetsClicked: facetsClicked - [currentFacet], rangesClicked: rangesClicked, q: q]
                                            else rowAction ActionIcon.FILTER * IconStyle.SCALE_DOWN, search, [facetsClicked: facetsClicked + [currentFacet], rangesClicked: rangesClicked, q: q]
                                            rowField v.name + "(${v.count})"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                col BlockSpec.Width.TWO_THIRD, {
                    ajaxBlock "results", {
                        table new UiTableSpecifier().ui {
                            for (def resp in response["response"]) {
                                def docId = resp['id'] as String
                                def hl = highlighting[docId]
                                def iSep = docId.lastIndexOf('-')
                                def solrSpecifier = mapSolrSpecifier[classMap[docId.substring(0, iSep)]]?.bValue
                                if (solrSpecifier) {
                                    def id = Long.parseLong(docId.substring(iSep + 1))
                                    row {
                                        rowColumn(2) {
                                            TaackGormClass c = TaackGormClassRegisterService.getTaackGormClass(solrSpecifier.type.name)
                                            rowAction(ActionIcon.SELECT * IconStyle.SCALE_DOWN, c.showMethod, id)
                                            rowField c.showLabel.call(id) as String
                                        }
                                    }
                                    for (def hli in hl) {
                                        row {
                                            rowField i18nMap[hli.key]
                                            rowField hli.value.join(', ')
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private final void indexAllClass(Class<? extends GormEntity> aClass, Map<Class<? extends GormEntity>, Pair<TaackSearchService.IIndexService, SolrSpecifier>> mapSolrSpecifier, TaackSearchService.IIndexService toIndex, SolrSpecifier solrSpecifier) {
        log.info "indexAll ${aClass}"
        try {
            toIndex.indexThose(aClass).each {
                SolrInputDocument d = new SolrInputDocument([:])
                try {
                    solrSpecifier.visitSolr(new SolrIndexerVisitor(d, it), it)
                } catch (e) {
                    log.error("Cannot index object: ${e.message}")
                }
                solrClient.add d
            }
        } catch (e) {
            log.error("Cannot index ${aClass}: ${e.message}")
        }
        log.info "indexAll ${aClass} ends"
        solrClient.commit()
        log.info "indexAll ${aClass} commit ends"
    }

    final void indexAll(Map<Class<? extends GormEntity>, Pair<TaackSearchService.IIndexService, SolrSpecifier>> mapSolrSpecifier) {
        solrClient.deleteByQuery("*:*")
        createSolrSchemas()
        mapSolrSpecifier.each {
            indexAllClass(it.key, mapSolrSpecifier, it.value.aValue, it.value.bValue)
        }
    }

    final void indexAllOnly(String name, Map<Class<? extends GormEntity>, Pair<TaackSearchService.IIndexService, SolrSpecifier>> mapSolrSpecifier) {
        solrClient.deleteByQuery("*:*")
        createSolrSchemas()
        mapSolrSpecifier.each {
            if (it.key.simpleName == name) indexAllClass(it.key, mapSolrSpecifier, it.value.aValue, it.value.bValue)
        }
    }

    private void createSolrSchemas() {
        try {
            def deleteDynamicFieldQuery = new SchemaRequest.DeleteDynamicField("*_noAccent")
            deleteDynamicFieldQuery.process(solrClient)
            def deleteFieldTypeQuery = new SchemaRequest.DeleteFieldType("text_noAccents")
            deleteFieldTypeQuery.process(solrClient)
            solrClient.commit()
        } catch (e) {
            log.error "${e.message}"
        }

        def definition = new FieldTypeDefinition()
        def analyzer = new AnalyzerDefinition()
        analyzer.charFilters = [["class": "solr.MappingCharFilterFactory", "mapping": "mapping-ISOLatin1Accent.txt"] as Map<String, Object>]
        analyzer.tokenizer = ["class": "solr.StandardTokenizerFactory"] as Map<String, Object>
        analyzer.filters = [["class": "solr.LowerCaseFilterFactory"] as Map<String, Object>, ["class": "solr.DoubleMetaphoneFilterFactory", "inject": "false"] as Map<String, Object>]
        definition.analyzer = analyzer
        definition.attributes = [name: "text_noAccents", "class": "solr.TextField"] as Map<String, Object>
        def fieldTypeRequest = new SchemaRequest.AddFieldType(definition)
        fieldTypeRequest.process(solrClient)
        def dynamicFieldRequest = new SchemaRequest.AddDynamicField([name: "*_noAccent", type: "text_noAccents"] as Map<String, Object>)
        dynamicFieldRequest.process(solrClient)
        solrClient.commit()
    }

    String fileContentToStringWithoutOcr(InputStream stream) {
        AutoDetectParser parser = new AutoDetectParser()
        BodyContentHandler handler = new BodyContentHandler(500_000)
        Metadata metadata = new Metadata()

        TesseractOCRConfig config = new TesseractOCRConfig()
        config.setSkipOcr(true)
        ParseContext context = new ParseContext()
        context.set(TesseractOCRConfig.class, config)
        parser.parse(stream, handler, metadata, context)
        handler.toString()
    }

    String fileContentToStringWithOcr(InputStream stream) {
        AutoDetectParser parser = new AutoDetectParser()
        BodyContentHandler handler = new BodyContentHandler(500_000)
        Metadata metadata = new Metadata()
        parser.parse(stream, handler, metadata)
        handler.toString()
    }

}
