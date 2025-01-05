package taack.render

import grails.artefact.controller.support.ResponseRenderer
import grails.compiler.GrailsCompileStatic
import grails.web.api.WebAttributes
import grails.web.databinding.DataBinder
import org.grails.core.io.ResourceLocator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import taack.ui.TaackUiConfiguration
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiShowSpecifier
import taack.ui.dsl.block.BlockSpec
import taack.wysiwyg.Asciidoc

import java.nio.file.Path
import java.util.zip.Inflater
/**
 * Service providing TQL support to scripts in the editor
 */
@GrailsCompileStatic
final class TaackEditorService implements WebAttributes, ResponseRenderer, DataBinder {

    @Autowired
    TaackUiConfiguration taackUiConfiguration

    @Autowired
    ResourceLocator assetResourceLocator

    private Path getScriptCachePath() {
        Path.of(taackUiConfiguration.root, 'cache', 'asciidoc', 'script')
    }

    private Path getAsciidocCachePath() {
        Path.of(taackUiConfiguration.root, 'cache', 'asciidoc', 'content')
    }

    def asciidocRenderScript(String script) {
        script = script.replaceAll('-', '+').replaceAll('_', '/')
        File cached = Path.of(scriptCachePath.toString(), script).toFile()
        if (cached.exists()) {
            render cached.text
        } else {
            byte[] b64 = Base64.getDecoder().decode(script)
            Inflater inflater = new Inflater()
            inflater.setInput(b64)

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
            byte[] buffer = new byte[1024]

            while (!inflater.finished()) {
                int decompressedSize = inflater.inflate(buffer)
                outputStream.write(buffer, 0, decompressedSize)
            }

             println(new String(outputStream.toByteArray()))

        }
    }

    UiBlockSpecifier asciidocBlockSpecifier(Class cl, String fileName) {
        InputStream resource = cl.getResourceAsStream(fileName)

        if (!resource) return new UiBlockSpecifier().ui {
            modal {
                show new UiShowSpecifier().ui {
                    inlineHtml("""<p>No $fileName Resource</p>""")
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

        new UiBlockSpecifier().ui {
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
    }
}