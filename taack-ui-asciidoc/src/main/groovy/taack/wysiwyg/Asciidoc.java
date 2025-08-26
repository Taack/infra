package taack.wysiwyg;


import org.asciidoctor.*;
import org.asciidoctor.ast.Document;
import taack.ui.TaackUiConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Translate Markdown to HTML using flexmark
 */
public class Asciidoc {

    private static Asciidoctor asciidoctor = null;
    public static final String pathAsciidocGenerated = TaackUiConfiguration.getRoot() + "/asciidoc";

    private static void initAsciidoctorJ() {
        try {
            Files.createDirectories(Path.of(pathAsciidocGenerated));
            asciidoctor = Asciidoctor.Factory.create();
            asciidoctor.requireLibrary("asciidoctor-diagram", "asciidoctor-revealjs");
        } catch (Throwable t) {
            System.out.println("Asciidoc::initAsciidoctorJ " + t.getMessage());
            t.printStackTrace();
        }
    }

    public static String getContentHtml(String content, String urlFileRoot) {
        return getContentHtml(content, urlFileRoot, null);
    }

    /**
     * Translate Asciidoctor string content to HTML, no inline
     *
     * @param content     The Asciidoc Text
     * @param urlFileRoot url prefix for external resources
     * @return The HTML results
     */
    public static String getContentHtml(String content, String urlFileRoot, SafeMode safeMode) {
        if (content != null) {
            initAsciidoctorJ();
            OptionsBuilder optionHasToc = Options.builder()
                    .safe(safeMode != null ? safeMode : SafeMode.SERVER)
                    .attributes(Attributes.builder().attribute("imagesoutdir", pathAsciidocGenerated).imagesDir(urlFileRoot + "?path=").build())
                    .option("parse_header_only", false);

            Document document = asciidoctor.load(content, optionHasToc.build());
            String html = document.convert();
//            asciidoctor.shutdown();
//            System.out.println(html);
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
        return getContentHtml(file, urlFileRoot, null);
    }

    public static String getContentHtml(File file, String urlFileRoot, SafeMode safeMode) {
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
                    .safe(safeMode != null ? safeMode : SafeMode.SERVER)
                    .toFile(false);
            //            asciidoctor.shutdown();
            return asciidoctor.convertFile(file, option.build());
        }
        return "";
    }
}
