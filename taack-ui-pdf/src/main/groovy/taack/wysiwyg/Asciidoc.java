package taack.wysiwyg;


import org.asciidoctor.*;
import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Translate Markdown to HTML using flexmark
 */
public class Asciidoc {

    private static Asciidoctor asciidoctor = null;

    private static void initAsciidoctorJ() {
        if (asciidoctor == null)
            try {
                asciidoctor = Asciidoctor.Factory.create();
            } catch (Throwable t) {
                System.out.println("Asciidoc::initAsciidoctorJ " + t.getMessage());
                t.printStackTrace();
            }
    }

    /**
     * Translate Asciidoctor string content to HTML, no inline
     *
     * @param content     The Asciidoc Text
     * @param urlFileRoot url prefix for external resources
     * @return The HTML results
     */
    public static String getContentHtml(String content, String urlFileRoot) {
        if (content != null) {
            initAsciidoctorJ();
            OptionsBuilder optionHasToc = Options.builder()
                    .attributes(Attributes.builder().imagesDir(urlFileRoot + "?path=").build())
                    .option("parse_header_only", false);

            Document document = asciidoctor.load(content, optionHasToc.build());
            String html = document.convert();
//            asciidoctor.shutdown();
            System.out.println(html);
            return html;
        }
        return "";
    }

    /**
     * Translate Asciidoctor file content to HTML, can inline other content
     *
     * @param file        The Asciidoc File
     * @param urlFileRoot url prefix for external resources
     * @return The HTML results
     */
    public static String getContentHtml(File file, String urlFileRoot) {
        if (file != null && file.exists()) {
            initAsciidoctorJ();
            OptionsBuilder option = Options.builder()
                    .attributes(Attributes.builder()
                            .imagesDir(urlFileRoot + "?path=")
                            .attribute("source-highlighter", "rouge")
                            .attribute("rouge-style", "monokai")
                            .attribute("icons", "font")
                            .build())
                    .option("parse_header_only", false)
                    .safe(SafeMode.SERVER)
                    .toFile(false);
            //            asciidoctor.shutdown();
            return asciidoctor.convertFile(file, option.build());
        }
        return "";
    }
}
