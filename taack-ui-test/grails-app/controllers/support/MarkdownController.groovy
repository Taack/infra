package support

import attachement.AttachmentUiService
import attachment.Attachment
import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.runtime.MethodClosure
import taack.render.TaackSaveService
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.wysiwyg.Markdown
import taack.wysiwyg.MarkdownCustomMenuButton

@GrailsCompileStatic
@Secured(['isAuthenticated()'])
class MarkdownController {
    TaackUiService taackUiService
    AttachmentUiService attachmentUiService
    TaackSaveService taackSaveService

    def showPreview(String body) {
        render Markdown.getContentHtml(body)
    }

    def serializeCustomMenuButtons() {
        def button1 = new MarkdownCustomMenuButton(title: "Image", icon: '<img width="20" height="20" src="/assets/icons/image.svg">', openModalUrl: '/markdown/selectAttachment')
//        def button2 = new MarkdownCustomMenuButton(...)
        render status: 200, text: ([button1/*, button2*/] as JSON).toString()
    }

    def selectAttachment() {
        if (params['directUpload'] == 'true') {
            redirect action: 'uploadAttachment', params: [directUpload: true, isAjax: true]
            return
        }
        taackUiService.show(new UiBlockSpecifier().ui {
            modal {
                inline(attachmentUiService.buildAttachmentsBlock(MarkdownController.&selectAttachmentCloseModal as MethodClosure, null, MarkdownController.&uploadAttachment as MethodClosure))
            }
        })
    }

    def selectAttachmentCloseModal(Attachment attachment) {
        UiBlockSpecifier block = new UiBlockSpecifier()
        block.ui {
            String src = "/attachment/preview/${attachment.id}"
            closeModal src, "<img alt='${attachment.toString()}' src='$src' width='400'>"
        }
        taackUiService.show(block)
    }

    def uploadAttachment() {
        taackUiService.show(new UiBlockSpecifier().ui {
            modal {
                    form AttachmentUiService.buildAttachmentForm(new Attachment(), MarkdownController.&saveAttachment as MethodClosure, [directUpload: params['directUpload'] == 'true'])
            }
        })
    }

    @Transactional
    def saveAttachment() {
        if (taackUiService.isProcessingForm()) {
            Attachment a = taackSaveService.save(Attachment)
            a.save(flush: true, failOnError: true)
            if (params['directUpload'] == 'true') {
                selectAttachmentCloseModal(a)
            } else {
                taackUiService.cleanForm()
                taackSaveService.displayBlockOrRenderErrors(a, new UiBlockSpecifier().ui {
                    closeModalAndUpdateBlock attachmentUiService.buildAttachmentsBlock(MarkdownController.&selectAttachmentCloseModal as MethodClosure, null, MarkdownController.&uploadAttachment as MethodClosure)
                })
            }
        } else {
            taackUiService.show(new UiBlockSpecifier().ui {
                inline(attachmentUiService.buildAttachmentsBlock(MarkdownController.&selectAttachmentCloseModal as MethodClosure, null, MarkdownController.&uploadAttachment as MethodClosure))
            })
        }
    }
}
