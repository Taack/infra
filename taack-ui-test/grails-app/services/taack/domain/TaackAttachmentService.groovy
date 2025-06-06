package taack.domain

import attachment.config.AttachmentContentType
import attachment.config.AttachmentContentTypeCategory
import grails.compiler.GrailsCompileStatic
import grails.util.Pair
import grails.web.api.ServletAttributes
import grails.web.api.WebAttributes
import grails.web.databinding.DataBinder
import jakarta.annotation.PostConstruct
import org.apache.commons.io.FileUtils
import org.grails.datastore.gorm.GormEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import attachment.Attachment
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.taack.IAttachmentConverter
import org.taack.IAttachmentPreviewConverter
import org.taack.IAttachmentShowIFrame
import taack.render.TaackSaveService
import taack.ui.TaackUiConfiguration

import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.FileImageInputStream
import javax.imageio.stream.ImageInputStream
import java.security.MessageDigest

@GrailsCompileStatic
class TaackAttachmentService implements WebAttributes, DataBinder, ServletAttributes {
    final Object imageConverter = new Object()

    static Map<String, File> filePaths = [:]

    TaackSearchService taackSearchService
    @Autowired
    TaackUiConfiguration taackUiConfiguration

    @Value('${intranet.root}')
    String intranetRoot

    String getStorePath() {
        intranetRoot + "/attachment/store"
    }

    String getAttachmentTmpPath() {
        intranetRoot + "/attachment/tmp"
    }

    String getAttachmentTxtPath() {
        intranetRoot + "/attachment/txt"
    }

    String getAttachmentStorePath() {
        intranetRoot + "/attachment/store"
    }

    enum PreviewFormat {
        DEFAULT(false, 240, 150),
        DEFAULT_PDF(true, 480, 300),
        PREVIEW_MEDIUM(false, 480, 300),
        PREVIEW_LARGE(false, 1280, 800),
        PREVIEW_LARGE_PDF(true, 1280, 800)

        PreviewFormat(boolean isPdf, int pixelWidth, int pixelHeight) {
            this.isPdf = isPdf
            this.pixelHeight = pixelHeight
            this.pixelWidth = pixelWidth
        }

        String getPreviewExtension() {
            isPdf ? "png" : "webp"
        }

        String attachmentPreviewFileName(Attachment attachment) {
            attachment.contentShaOne.strip() + '.' + previewExtension
        }

        final boolean isPdf
        final int pixelHeight
        final int pixelWidth
    }

    String previewPath(final PreviewFormat format) {
        intranetRoot + "/attachment/preview/${format.toString()}"
    }

    String attachmentFileName(final Attachment attachment) {
        if (attachment.originalName.contains('.'))
            attachment.contentShaOne + attachment.originalName.substring(attachment.originalName.lastIndexOf('.'))
        else
            attachment.contentShaOne + ".NONE"
    }

    String attachmentPath(final Attachment attachment) {
        storePath + '/' + attachmentFileName(attachment)
    }

    File attachmentFile(final Attachment attachment) {
        new File(attachmentPath(attachment))
    }

    String attachmentTxtPath(final Attachment attachment) {
        if (attachment.originalName.contains('.'))
            attachmentTxtPath + '/' + attachment.contentShaOne + attachment.originalName.substring(attachment.originalName.lastIndexOf('.')) + ".txt"
        else
            attachmentTxtPath + '/' + attachment.contentShaOne + ".NONE" + ".txt"
    }

    String attachmentPreviewPath(final PreviewFormat previewFormat, final Attachment attachment) {
        previewPath(previewFormat) + '/' + previewFormat.attachmentPreviewFileName(attachment)
    }

    enum ConvertMode {
        DIRECT_CONVERT,
        UNO_CONVERTER,
        LO_CONVERT_TEXT_DOCUMENT("writer_pdf_Export"),
        LO_CONVERT_SPREADSHEET("calc_pdf_Export"),
        LO_CONVERT_PRESENTATION("impress_pdf_Export")

        ConvertMode(final String pdfFilter = null) {
            this.pdfFilter = pdfFilter
        }

        final String pdfFilter
    }

    enum ConvertExtensions {
        ICO(".ico", "image.webp", ConvertMode.DIRECT_CONVERT),
        WEBP(".webp", "image.webp", ConvertMode.DIRECT_CONVERT),
        JPG(".jpg", "image.webp", ConvertMode.DIRECT_CONVERT),
        JPEG(".jpeg", "image.webp", ConvertMode.DIRECT_CONVERT),
        PNM(".pnm", "image.webp", ConvertMode.DIRECT_CONVERT),
        PNG(".png", "image.webp", ConvertMode.DIRECT_CONVERT),
        PIX(".pix", "image.webp", ConvertMode.DIRECT_CONVERT),
        PDF(".pdf", "image.webp", ConvertMode.DIRECT_CONVERT),
        TIF(".tif", "image.webp", ConvertMode.DIRECT_CONVERT),
        SVG(".svg", "image.webp", ConvertMode.DIRECT_CONVERT, false),
        ODT(".odt", "doc.webp", ConvertMode.UNO_CONVERTER),
        DOCX(".docx", "doc.webp", ConvertMode.UNO_CONVERTER),
        DOC(".doc", "doc.webp", ConvertMode.UNO_CONVERTER),
        XLS(".xls", "ods.webp", ConvertMode.UNO_CONVERTER),
        XLSX(".xlsx", "ods.webp", ConvertMode.UNO_CONVERTER),
        ODS(".ods", "ods.webp", ConvertMode.UNO_CONVERTER),
        PPT(".ppt", "odp.webp", ConvertMode.UNO_CONVERTER),
        PPTX(".pptx", "odp.webp", ConvertMode.UNO_CONVERTER),
        ODP(".odp", "odp.webp", ConvertMode.UNO_CONVERTER)

        ConvertExtensions(final String extension, final String icon,
                          final ConvertMode convertMode,
                          final boolean changeExtension = true) {
            this.extension = extension
            this.icon = icon
            this.convertMode = convertMode
            this.changeExtension = changeExtension
        }
        final String extension
        final String icon
        final ConvertMode convertMode
        final boolean changeExtension

        static ConvertExtensions fileConvertExtensions(Attachment a) {
            if (a.originalName.lastIndexOf('.') == -1) return null
            final String fileExtension = a.originalName.substring(a.originalName.lastIndexOf('.'))
            values().find { it.extension == fileExtension?.toLowerCase() }
        }
    }

    static Map<String, IAttachmentPreviewConverter> additionalPreviewConverter = [:]
    static Map<String, Pair<List<String>, IAttachmentConverter>> additionalConverter = [:]
    static Map<String, IAttachmentShowIFrame> additionalShow = [:]

    @PostConstruct
    void init() {
        log.info "init"
        FileUtils.forceMkdir(new File(storePath))
        FileUtils.forceMkdir(new File(attachmentTmpPath))
        FileUtils.forceMkdir(new File(attachmentTxtPath))
        for (PreviewFormat f : PreviewFormat.values()) {
            FileUtils.forceMkdir(new File(previewPath(f)))
        }

        TaackSaveService.registerFieldCustomSavingClosure("filePath", { GormEntity gormEntity, Map params ->
            if (gormEntity.hasProperty("filePath")) {
                final List<MultipartFile> mfl = (request as MultipartHttpServletRequest).getFiles("filePath")
                final mf = mfl.first()
                if (mf.size > 0) {
                    final String sha1ContentSum = MessageDigest.getInstance("SHA1").digest(mf.bytes).encodeHex().toString()
                    final String p = sha1ContentSum + "." + (mf.originalFilename.substring(mf.originalFilename.lastIndexOf('.') + 1) ?: "NONE")
                    final String d = (filePaths.get(controllerName) ?: attachmentStorePath)
                    File target = new File(d + "/" + p)
                    mf.transferTo(target)

                    gormEntity["filePath"] = p
                    if (gormEntity.hasProperty("contentType")) {
                        gormEntity["contentType"] = mf.contentType
                        if (gormEntity.hasProperty("contentTypeEnum")) {
                            gormEntity["contentTypeEnum"] = AttachmentContentType.fromMimeType(mf.contentType)
                        }
                    }
                    if (gormEntity.hasProperty("originalName")) {
                        gormEntity["originalName"] = mf.originalFilename
                    }
                    if (gormEntity.hasProperty("md5sum")) {
                        gormEntity["md5sum"] = MessageDigest.getInstance("MD5").digest(mf.bytes).encodeHex().toString()
                    }
                    if (gormEntity.hasProperty("contentShaOne")) {
                        gormEntity["contentShaOne"] = sha1ContentSum
                    }
                    if (gormEntity.hasProperty("fileSize")) {
                        gormEntity["fileSize"] = mf.size
                    }
                    if (gormEntity.hasProperty("width")) {
                        final String suffix = mf.name.substring(mf.name.lastIndexOf('.') + 1)
                        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix)
                        while (iter.hasNext()) {
                            ImageReader reader = iter.next()
                            try {
                                ImageInputStream stream = new FileImageInputStream(target)
                                reader.setInput(stream)
                                int width = reader.getWidth(reader.getMinIndex())
                                int height = reader.getHeight(reader.getMinIndex())
                                gormEntity["width"] = width
                                if (gormEntity.hasProperty("height")) gormEntity["height"] = height
                                break
                            } catch (IOException e) {
                                log.warn "Error reading: " + mf.name, e
                            } finally {
                                reader.dispose()
                            }
                        }
                    }
                }
            }
        })
    }

    File attachmentPreview(final Attachment attachment, PreviewFormat previewFormat = PreviewFormat.DEFAULT) {
        if (!attachment) return new File("${taackUiConfiguration.resources}/noPreview.${previewFormat.previewExtension}")
        final File preview = new File(attachmentPreviewPath(previewFormat, attachment))
        if (preview.exists()) {
            return preview
        } else {
            final ConvertExtensions ce = ConvertExtensions.fileConvertExtensions(attachment)
            try {
                if (ce && ce.convertMode == ConvertMode.DIRECT_CONVERT) {
                    synchronized (imageConverter) {
                        String cmd = "convert ${attachmentPath(attachment)}[0] -resize ${previewFormat.pixelWidth + 'x' + previewFormat.pixelHeight} ${preview.path}"
                        log.info "AUO TaackSimpleAttachmentService executing $cmd"
                        Process p = cmd.execute()
                        p.consumeProcessOutput()
                        p.waitForOrKill(30 * 1000)
                    }
                    if (preview.exists()) {
                        return preview
                    }
                } else if (ce && ce.convertMode == ConvertMode.UNO_CONVERTER) {
                    log.info "AUO TaackSimpleAttachmentService executing unoconv -f pdf -e PageRange=1-1 --stdout ${attachmentPath(attachment)}"
                    synchronized (imageConverter) {
                        def p = "unoconv -f pdf -e PageRange=1-1 --stdout ${attachmentPath(attachment)}".execute() | "convert -resize ${previewFormat.pixelWidth + 'x' + previewFormat.pixelHeight} - ${preview.path}".execute()
                        p.waitForOrKill(30 * 1000)
                    }
                    if (preview.exists()) {
                        return preview
                    }
                } else if (!ce) {
                    final String fileExtension = attachment.originalName.substring(attachment.originalName.lastIndexOf('.') + 1)
                    IAttachmentPreviewConverter previewConverter = additionalPreviewConverter[fileExtension]
                    if (previewConverter) {
                        previewConverter.createWebpPreview(attachment, preview.path)
                        if (preview.exists()) {
                            return preview
                        }
                    }
                }
            } catch (IOException eio) {
                log.error "attachmentPreview killed before finishing for ${attachment.name} ${eio}"
            }
        }
        return new File("${taackUiConfiguration.resources}/noPreview.${previewFormat.previewExtension}")
    }

    static void registerPreviewConverter(IAttachmentPreviewConverter previewConverter) {
        for (String extension in previewConverter.previewManagedExtensions) {
            additionalPreviewConverter.put(extension, previewConverter)
        }
    }

    static void registerConverter(IAttachmentConverter converter) {
        for (def extensionEntry in converter.supportedExtensionConversions) {
            additionalConverter.put(extensionEntry.key, new Pair(extensionEntry.value, converter))
        }
    }

    static List<String> converterExtensions(Attachment attachment) {
        additionalConverter.get(attachment.extension?.toLowerCase())?.aValue
    }

    static File convertExtension(Attachment attachment, String extension) {
        additionalConverter.get(attachment.extension?.toLowerCase())?.bValue?.convertTo(attachment, extension?.toLowerCase())
    }

    static void registerAdditionalShow(IAttachmentShowIFrame showIFrame) {
        for (String extension in showIFrame.showIFrameManagedExtensions) {
            additionalShow.put(extension, showIFrame)
        }
    }

    static IAttachmentShowIFrame additionalShowIFrame(Attachment attachment) {
        String name = attachment.originalName.substring(attachment.originalName.lastIndexOf('.') + 1)
        additionalShow[name]
    }

    static String showIFrame(Attachment attachment) {
        additionalShowIFrame(attachment)?.createShowIFrame(attachment)
    }

    void downloadAttachment(Attachment attachment) {
        if (!attachment) return
        def response = webRequest.currentResponse
        File f = new File(attachmentPath(attachment))
        if (f.exists()) {
            response.setContentType(attachment.contentType)
            response.setHeader("Content-disposition", "attachment;filename=\"${URLEncoder.encode(attachment.getName(), "UTF-8")}\"")
            response.outputStream << f.bytes
        } else {
            log.error "No file: ${f.path}"
        }
    }

    String attachmentContent(Attachment attachment) {
        if (!attachment.originalName) return null
        File txt = new File(attachmentTxtPath(attachment))
        if (txt.exists()) return txt.text
        File a = new File(attachmentPath(attachment))
        if (a.exists()) {

            try (InputStream stream = new FileInputStream(a)) {
                if (attachment.contentTypeCategoryEnum == AttachmentContentTypeCategory.IMAGE) {
                    log.info "creating ${txt.path} with OCR"
                    txt << taackSearchService.fileContentToStringWithOcr(stream)
                    return txt.text
                } else {
                    log.info "creating ${txt.path} without OCR"
                    txt << taackSearchService.fileContentToStringWithoutOcr(stream)
                    return txt.text
                }
            } catch (e) {
                log.error e.message
                txt << e.message
                return txt.text
            }
        }
        null
    }


}