package taack.render

import grails.artefact.controller.support.ResponseRenderer
import grails.compiler.GrailsCompileStatic
import grails.web.api.WebAttributes
import grails.web.databinding.DataBinder
import org.springframework.beans.factory.annotation.Autowired
import taack.ui.TaackUiConfiguration

import java.nio.file.Path
import java.util.zip.Inflater
/**
 * Service providing TQL support to scripts in the editor
 */
@GrailsCompileStatic
final class TaackEditorService implements WebAttributes, ResponseRenderer, DataBinder {

    @Autowired
    TaackUiConfiguration taackUiConfiguration

    private Path getCachePath() {
        Path.of(taackUiConfiguration.root, 'script', 'cache')
    }

    def asciidocRenderScript(String script) {
        script = script.replaceAll('-', '+').replaceAll('_', '/')
        File cached = Path.of(cachePath.toString(), script).toFile()
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


}