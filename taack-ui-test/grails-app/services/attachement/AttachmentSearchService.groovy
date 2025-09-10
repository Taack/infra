package attachement

import attachment.config.AttachmentContentTypeCategory
import crew.AttachmentController
import grails.compiler.GrailsCompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import attachment.Attachment
import taack.domain.TaackAttachmentService
import taack.domain.TaackGormClass
import taack.domain.TaackGormClassRegisterService
import taack.domain.TaackSearchService
import taack.solr.SolrFieldType
import taack.solr.SolrSpecifier
import taack.ui.dsl.UiBlockSpecifier

import jakarta.annotation.PostConstruct

@GrailsCompileStatic
class AttachmentSearchService implements TaackSearchService.IIndexService {

    static lazyInit = false

    TaackSearchService taackSearchService
    TaackAttachmentService taackAttachmentService

    @PostConstruct
    private void init() {
        TaackSearchService.registerSolrSpecifier(this, new SolrSpecifier(Attachment, { Attachment a ->
            a ?= new Attachment()
            String content = taackAttachmentService.attachmentContent(a)
            indexField SolrFieldType.TXT_GENERAL, a.originalName_
            if (content)
                indexField SolrFieldType.TXT_GENERAL, 'fileContent', content
            indexField SolrFieldType.POINT_STRING, 'contentTypeCategoryEnum', true, a.contentTypeCategoryEnum?.toString()
            indexField SolrFieldType.DATE, 0.5f, true, a.dateCreated_
            indexField SolrFieldType.POINT_STRING, 'userCreated', 0.5f, true, a.userCreated?.username
        }))
        TaackGormClassRegisterService.register(
                new TaackGormClass(Attachment.class).builder
                        .setShowMethod(AttachmentController.&showAttachment as MethodClosure)
                        .setShowLabel({ Long id ->
                            def a = Attachment.read(id)
                            return "Attachment: ${a.originalName} ($id)"
                        }).build()
        )
    }

    @Override
    List<? extends GormEntity> indexThose(Class<? extends GormEntity> toIndex) {
        if (toIndex.isAssignableFrom(Attachment)) return Attachment.findAllByActiveAndContentTypeCategoryEnumInList(true, [AttachmentContentTypeCategory.DOCUMENT, AttachmentContentTypeCategory.PRESENTATION, AttachmentContentTypeCategory.SPREADSHEET])
        else null
    }

    UiBlockSpecifier buildSearchBlock(String q) {
        taackSearchService.search(q, AttachmentController.&search as MethodClosure, Attachment)
    }
}
