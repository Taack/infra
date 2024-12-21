package taack.wysiwyg;


import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.ast.Document;

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
            }
    }

    /**
     * Translate Markdown content to HTML
     * @param content The Markdown Text
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
            asciidoctor.shutdown();
            System.out.println(html);
            return html;
        }
        return "";
    }
}
