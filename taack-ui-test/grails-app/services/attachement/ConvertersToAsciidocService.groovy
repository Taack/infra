package attachement

import cms.CmsController
import cms.CmsImage
import cms.CmsPage
import crew.User
import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.SpringSecurityService
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import taack.domain.TaackAttachmentService
import taack.render.TaackEditorService

import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.FileImageInputStream
import javax.imageio.stream.ImageInputStream
import javax.swing.text.html.HTML
import java.nio.file.Files
import java.security.MessageDigest

@GrailsCompileStatic
class ConvertersToAsciidocService {

    SpringSecurityService springSecurityService
    TaackEditorService taackEditorService

    final private boolean debug = true

    final CmsImage saveImage(CmsPage page, String path, byte[] image) {
        final String sha1ContentSum = MessageDigest.getInstance('SHA1').digest(image).encodeHex().toString()
        CmsImage cmsImage = CmsImage.findByContentShaOne(sha1ContentSum)
        if (cmsImage) return cmsImage
        final String name = path.substring(path.lastIndexOf('/') + 1)
        final String p = sha1ContentSum + '.' + (name.substring(name.lastIndexOf('.') + 1) ?: 'NONE')
        File target = new File(CmsController.cmsFileRoot + '/' + p)
        target << image

        cmsImage = new CmsImage()
        cmsImage.cmsPage = page
        cmsImage.dateCreated = new Date()
        cmsImage.lastUpdated = cmsImage.dateCreated
        cmsImage.filePath = p
        cmsImage.contentType = Files.probeContentType(target.toPath())
        cmsImage.originalName = name
        cmsImage.contentShaOne = sha1ContentSum

        final String suffix = name.substring(name.lastIndexOf('.') + 1)
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix)
        while (iter.hasNext()) {
            ImageReader reader = iter.next()
            try {
                ImageInputStream stream = new FileImageInputStream(target)
                reader.setInput(stream)
                int width = reader.getWidth(reader.getMinIndex())
                int height = reader.getHeight(reader.getMinIndex())
                cmsImage.width = width
                cmsImage.height = height
                break
            } catch (IOException e) {
                log.error("Error reading: $name, $e")
            } finally {
                reader.dispose()
            }
        }
        cmsImage.userCreated = springSecurityService.currentUser as User
        cmsImage.userUpdated = springSecurityService.currentUser as User
        cmsImage.save(flush: true)
        if (cmsImage.hasErrors()) log.error("${cmsImage.errors}")
        cmsImage
    }

    String convert(CmsPage page, InputStream inputStream) {
        taackEditorService.convert(new TaackEditorService.ISaveImage() {
            @Override
            String saveImage(String imagePath, byte[] image) {
                return saveImage(page, imagePath, image)
            }
        }, inputStream)
    }

    String convertFromHtml(String html) {
        taackEditorService.convertFromHtml(html)
    }
}
