package taack.render

import grails.artefact.controller.support.ResponseRenderer
import grails.compiler.GrailsCompileStatic
import grails.web.api.WebAttributes
import grails.web.databinding.DataBinder
import org.grails.core.io.ResourceLocator
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import taack.ui.TaackUiConfiguration
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiShowSpecifier
import taack.ui.dsl.block.BlockSpec
import taack.wysiwyg.Asciidoc

import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.Inflater
/**
 * Service providing TQL support to scripts in the editor
 */
@GrailsCompileStatic
final class TaackEditorService implements WebAttributes, ResponseRenderer, DataBinder {

    enum ImageExtension {
        png('png'), jpg('jpeg'), svg('svg+xml')

        ImageExtension(String mimeImage) {
            this.mimeImage = mimeImage
        }

        final String mimeImage
    }

    @Value('${intranet.root}')
    String rootPath

    @Autowired
    ResourceLocator assetResourceLocator

//    private Path getScriptCachePath() {
//        Path.of(TaackUiConfiguration.root, 'cache', 'asciidoc', 'script')
//    }

    private Path getAsciidocCachePath() {
        Path.of(rootPath, 'cache', 'asciidoc', 'content')
    }

//    def asciidocRenderScript(String script) {
//        script = script.replaceAll('-', '+').replaceAll('_', '/')
//        File cached = Path.of(scriptCachePath.toString(), script).toFile()
//        if (cached.exists()) {
//            render cached.text
//        } else {
//            byte[] b64 = Base64.getDecoder().decode(script)
//            Inflater inflater = new Inflater()
//            inflater.setInput(b64)
//
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
//            byte[] buffer = new byte[1024]
//
//            while (!inflater.finished()) {
//                int decompressedSize = inflater.inflate(buffer)
//                outputStream.write(buffer, 0, decompressedSize)
//            }
//
//            println(new String(outputStream.toByteArray()))
//
//        }
//    }

    UiBlockSpecifier asciidocBlockSpecifier(Class cl, String fileName) {
        String path = params['path']
        String fnPath = '/' + fileName.split('/')[1]
        if (path) {
            ImageExtension imageExtension = path[-3..-1] as ImageExtension
            GrailsWebRequest webUtils = WebUtils.retrieveGrailsWebRequest()
            webUtils.currentResponse.setContentType("image/${imageExtension.mimeImage}")
            webUtils.currentResponse.setHeader("Content-disposition", "attachment;filename=${fileName.split('/')[-1]}")
            webUtils.currentResponse.outputStream << cl.getResourceAsStream(fnPath + path)
            try {
                webUtils.currentResponse.outputStream.flush()
                webUtils.currentResponse.outputStream.close()
                webRequest.renderView = false
            } catch (e) {
                log.error "${e.message}"
            }
            return null
        } else {
            InputStream resource = cl.getResourceAsStream(fileName)

            if (!resource) {
                return new UiBlockSpecifier().ui {
                    modal {
                        show new UiShowSpecifier().ui {
                            inlineHtml("""<p>No $fileName Resource</p>""")
                        }
                    }
                }
            }
            File resourceFile = Path.of(asciidocCachePath.toString(), fileName).toFile()
            if (!resourceFile.exists()) {
                resourceFile.getParentFile().mkdirs()
                resourceFile.createNewFile()
            }
            resourceFile.text = resource.text
            StringBuffer out = new StringBuffer()
            out.append Asciidoc.getContentHtml(resourceFile, "")
            Resource r = assetResourceLocator.findResourceForURI("asciidoc.js")
            if (r?.exists()) {
                out.append('\n')
                out.append("""<script postexecute="true">""")
                out.append(r.inputStream.text)
                out.append("</script>")
                out.append('\n')
            }
            Closure<BlockSpec> blockSpecClosure = BlockSpec.buildBlockSpec {
                row {
                    col(BlockSpec.Width.QUARTER) {
                    }
                    col(BlockSpec.Width.THREE_QUARTER) {
                        show new UiShowSpecifier().ui {
                            inlineHtml out.toString(), "asciidocMain"
                        }
                    }
                }
            }

            new UiBlockSpecifier().ui {
                inline blockSpecClosure
            }
        }
    }
}