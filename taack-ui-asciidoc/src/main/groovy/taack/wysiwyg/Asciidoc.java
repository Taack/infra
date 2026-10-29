package taack.wysiwyg;


import org.asciidoctor.*;
import org.asciidoctor.ast.Document;
import taack.ui.TaackUiConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.asciidoctor.log.LogRecord;

/**
 * Translate Markdown to HTML using flexmark
 */
public class Asciidoc {

    public static final String pathAsciidocGenerated = TaackUiConfiguration.getRoot() + "/asciidoc";
    private static final Asciidoctor asciidoctor = initAsciidoctorJ();

    private static Asciidoctor initAsciidoctorJ() {
        try {
            Files.createDirectories(Path.of(pathAsciidocGenerated));
            Asciidoctor asciidoctor = Asciidoctor.Factory.create();
            asciidoctor.requireLibrary("asciidoctor-diagram", "asciidoctor-revealjs");
            return asciidoctor;
        } catch (Throwable t) {
            System.out.println("Asciidoc::initAsciidoctorJ " + t.getMessage());
            t.printStackTrace();
            return null;
        }
    }

    /**
     * Translate Asciidoctor string content to HTML, no inline
     *
     * @param content     The Asciidoc Text
     * @param urlFileRoot url prefix for external resources
     * @return The HTML results
     */
    public static String getContentHtml(String content, String urlFileRoot, boolean server) {
        if (content != null && asciidoctor != null) {
            OptionsBuilder optionHasToc = Options.builder()
                    .safe(server ? SafeMode.SERVER : SafeMode.UNSAFE)
                    .attributes(Attributes.builder().attribute("imagesoutdir", pathAsciidocGenerated).imagesDir(urlFileRoot + "?path=").experimental(true).showTitle(true).build())
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
     * A problem reported by Asciidoctor while converting a document.
     */
    public static final class Problem {
        /** 1-based line number, or 0 when Asciidoctor gives no location */
        public final int line;
        /** Asciidoctor severity (DEBUG, INFO, WARN, ERROR, FATAL, UNKNOWN) */
        public final String severity;
        /** The message, without location */
        public final String message;

        public Problem(int line, String severity, String message) {
            this.line = line;
            this.severity = severity;
            this.message = message;
        }
    }

    // Validation must not run asciidoctor-diagram: it would render every diagram on each keystroke.
    private static Asciidoctor validator;
    private static final List<LogRecord> validatorLogs = new ArrayList<>();

    /**
     * Convert the content without writing anything and return the problems reported by Asciidoctor.
     * Intended for editor diagnostics, so diagrams are not rendered.
     *
     * @param content The Asciidoc Text
     * @return the problems, in the order Asciidoctor reported them
     */
    public static synchronized List<Problem> validate(String content) {
        List<Problem> res = new ArrayList<>();
        if (content == null) return res;
        if (validator == null) {
            validator = Asciidoctor.Factory.create();
            validator.registerLogHandler(validatorLogs::add);
        }
        validatorLogs.clear();
        try {
            Options options = Options.builder()
                    .safe(SafeMode.SECURE)
                    .sourcemap(true)
                    .toFile(false)
                    .build();
            validator.load(content, options).convert();
        } catch (Throwable t) {
            res.add(new Problem(0, "ERROR", String.valueOf(t.getMessage())));
        }
        for (LogRecord r : validatorLogs) {
            int line = r.getCursor() != null ? r.getCursor().getLineNumber() : 0;
            res.add(new Problem(line, r.getSeverity().name(), r.getMessage()));
        }
        validatorLogs.clear();
        return res;
    }

    public static String getContentHtml(String content) {
        return getContentHtml(content, "/taackAsciidoc/renderDiagram", false);
    }

    public static String getContentHtml(File file, String urlFileRoot, boolean server) {
        if (file != null && file.exists() && asciidoctor != null) {
            OptionsBuilder option = Options.builder()
                    .attributes(Attributes.builder()
                            .imagesDir(urlFileRoot + "?path=")
                            .attribute("source-highlighter", "rouge")
                            .attribute("rouge-style", "monokai")
                            .attribute("icons", "font")
                            .build())
                    .option("parse_header_only", false)
                    .safe(server ? SafeMode.SERVER : SafeMode.UNSAFE)
                    .toFile(false);
            //            asciidoctor.shutdown();
            return asciidoctor.convertFile(file, option.build());
        }
        return "";
    }
}
