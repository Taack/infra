package taack.asciidoc.extension;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry;

public class TqlExtension implements ExtensionRegistry {
    @Override
    public void register(Asciidoctor asciidoctor) {
        asciidoctor.javaExtensionRegistry().block(new TqlBlockProcessor());
    }
}
