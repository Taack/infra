package taack.render

import grails.compiler.GrailsCompileStatic
import grails.web.api.WebAttributes
import grails.web.databinding.DataBinder
import org.apache.xerces.dom.TextImpl
import org.asciidoctor.*
import org.grails.core.io.ResourceLocator
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import org.odftoolkit.odfdom.doc.OdfDocument
import org.odftoolkit.odfdom.dom.element.draw.DrawTextBoxElement
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement
import org.odftoolkit.odfdom.dom.element.table.TableTableElement
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement
import org.odftoolkit.odfdom.dom.element.text.TextListItemElement
import org.odftoolkit.odfdom.dom.element.text.TextSequenceElement
import org.odftoolkit.odfdom.dom.element.text.TextSoftPageBreakElement
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextHeading
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextList
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextSpan
import org.odftoolkit.odfdom.pkg.OdfPackage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiShowSpecifier
import taack.ui.dsl.block.BlockSpec
import taack.wysiwyg.Asciidoc

import java.nio.file.Path
/**
 * Service providing TQL support to scripts in the editor
 */
@GrailsCompileStatic
final class TaackEditorService implements WebAttributes, DataBinder {

    private Asciidoctor asciidoctor

    /**
     * Interface containing a callback to save images contained in an ODF doc
     */
    interface ISaveImage {
        String saveImage(String imagePath, byte[] image)
    }

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

    private Path getAsciidocCachePath() {
        Path.of(rootPath, 'cache', 'asciidoc', 'content')
    }

    UiBlockSpecifier asciidocBlockSpecifier(Class cl, String fileName) {
        String path = params['path']
        String fnPath = '/' + fileName.split('/')[1]
        if (path) {
            ImageExtension imageExtension = path[-3..-1] as ImageExtension
            GrailsWebRequest webUtils = WebUtils.retrieveGrailsWebRequest()
            webUtils.currentResponse.setContentType("image/${imageExtension.mimeImage}")
            webUtils.currentResponse.setHeader('Content-disposition', "attachment;filename=${fileName.split('/')[-1]}")
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
                            inlineHtml("<p>No $fileName Resource</p>")
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
            out.append Asciidoc.getContentHtml(resourceFile, '', false)
            Resource r = assetResourceLocator.findResourceForURI('asciidoc.js')
            if (r?.exists()) {
                out.append('\n')
                out.append('<script postexecute="true">')
                out.append(r.inputStream.text)
                out.append('</script>')
                out.append('\n')
            }
            Closure<BlockSpec> blockSpecClosure = BlockSpec.buildBlockSpec {
                row {
                    col(BlockSpec.Width.QUARTER) {
                    }
                    col(BlockSpec.Width.THREE_QUARTER) {
                        show new UiShowSpecifier().ui {
                            inlineHtml out.toString(), 'asciidocMain'
                        }
                    }
                }
            }

            new UiBlockSpecifier().ui {
                inline blockSpecClosure
            }
        }
    }


    final private boolean debug = true

    private String traverseNodeBefore(ISaveImage iSaveImage, OdfPackage odfPackage, Node node, Node... parents) {
        if (node instanceof OdfDrawImage) {

            byte[] imageBytes = odfPackage.getBytes(node.imageUri.path)
            String originalName = iSaveImage.saveImage(node.imageUri.path, imageBytes)

            boolean softBreakAfter = (parents.findAll { it instanceof OdfTextParagraph }?.last()?.childNodes?.find { it instanceof TextSoftPageBreakElement }) != null
            return (softBreakAfter ? '' : '\n') + 'image:' + (softBreakAfter ? '' : ':') + originalName + '[]' + (softBreakAfter ? ' ' : '\n')
        } else if (node instanceof OdfTextParagraph) {
            int numberOfParent = (int) parents.length
            return numberOfParent == 3 ? '\n' : ''
        } else if (node instanceof TextImpl) {
            return node.textContent
        } else if (node instanceof OdfTextSpan) {
            String s = node.attributes.getNamedItem('text:style-name').nodeValue

            if (s == 'T1') {
                return '**'
            } else if (s == 'T2') {
                return '_'
            }
        } else if (node instanceof OdfTextHeading) {
            OdfTextHeading heading = node
            if (heading.attributes.getNamedItem('text:outline-level')) {
                int level = Integer.parseInt(heading.attributes.getNamedItem('text:outline-level').nodeValue)
                return '\n' + '=' * level + ' '
            }
        } else if (node instanceof OdfTextList) {
            int numberOfParent = (int) parents.count { it instanceof OdfTextList }
            return numberOfParent == 1 ? '\n' : ''
        } else if (node instanceof TextListItemElement) {
            int numberOfParent = (int) parents.count { it instanceof OdfTextList }
            return '*' * numberOfParent + ' '
        } else if (node instanceof OdfDrawFrame) {
        } else if (node instanceof DrawTextBoxElement) {
        } else if (node instanceof TextSequenceElement) {
        } else if (node instanceof TableTableElement) {
            return '\n|==='
        } else if (node instanceof TableTableColumnElement) {
        } else if (node instanceof TableTableRowElement) {
            return '\n'
        } else if (node instanceof TableTableCellElement) {
            return '| '
        }
        return ''
    }

    private static String traverseNodeAfter(Node node, Node... parents) {
        if (node instanceof OdfTextSpan) {
            String s = node.attributes.getNamedItem('text:style-name').nodeValue

            if (s == 'T1') {
                return '**'
            } else if (s == 'T2') {
                return '_'
            }
        } else if (node instanceof OdfTextParagraph) {
            boolean inTable = (parents.find { it instanceof TableTableElement }) != null
            return !inTable ? '\n' : ''
        } else if (node instanceof OdfTextHeading) {
            return '\n'
        } else if (node instanceof TableTableElement) {
            return '\n|===\n'
        }
        ''
    }

    private void treeNode(ISaveImage iSaveImage, OdfPackage odfPackage, NodeList entry, StringBuffer asciidoc, Node... parents) {
        int indent = parents ? parents.size() : 0
        for (int i = 0; i < entry.length; i++) {
            Node n = entry.item(i)
            if (debug) println("  " * indent + (n.localName ? n.localName + '(' + n.class + ')' : n.class) + "\t${n.textContent}")
            if (n instanceof OdfDrawImage) {
                OdfDrawImage image = n as OdfDrawImage
                if (debug) println("  " * (indent + 1) + image.imageUri)
            } else if (n instanceof OdfTextParagraph) {
            } else if (n instanceof TextImpl) {
                TextImpl text = n
                if (debug) println("  " * (indent + 1) + text.attributes)
            } else if (n instanceof OdfTextHeading) {
                OdfTextHeading heading = n
                if (debug) {
                    println("  " * (indent + 1) + heading.attributes.length)
                    for (int j = 0; j < heading.attributes.length; j++) {
                        println("  " * (indent + 1) + heading.attributes.item(j))
                    }
                }
            } else if (n instanceof TextListItemElement) {
                TextListItemElement listItemElement = n
                if (debug) for (int j = 0; j < listItemElement.attributes.length; j++) {
                    println("  " * (indent + 1) + listItemElement.attributes.item(j))
                }

            } else if (n instanceof OdfTextSpan) {
                OdfTextSpan span = n
                if (debug) {
                    println("  " * (indent + 1) + span.attributes.getNamedItem('style-name'))
                    for (int j = 0; j < span.attributes.length; j++) {
                        println("  " * (indent + 1) + span.attributes.item(j))
                    }
                }
            }

            if (n.childNodes.length > 0) {
                if (n instanceof OdfTextParagraph && n.childNodes.find({ it instanceof TextSequenceElement })) {
                    asciidoc.append('.')
                    asciidoc.append(n.textContent.substring(n.textContent.indexOf(':') + 2))
                    OdfDrawFrame frame = n.childNodes.find { it instanceof OdfDrawFrame } as OdfDrawFrame
                    if (frame) {
                        asciidoc.append(traverseNodeBefore(iSaveImage, odfPackage, frame, parents + frame))
                        treeNode(iSaveImage, odfPackage, frame.childNodes, asciidoc, parents + frame)
                        asciidoc.append(traverseNodeAfter(frame, parents + frame))
                    }
                } else {
                    asciidoc.append(traverseNodeBefore(iSaveImage, odfPackage, n, parents + n))
                    treeNode(iSaveImage, odfPackage, n.childNodes, asciidoc, parents + n)
                    asciidoc.append(traverseNodeAfter(n, parents + n))
                }
            } else {
                asciidoc.append(traverseNodeBefore(iSaveImage, odfPackage, n, parents + n))
            }
        }
    }

    String convert(ISaveImage iSaveImage, InputStream inputStream) {
        OdfDocument d = OdfDocument.loadDocument(inputStream)
        OdfPackage pack = d.package
        StringBuffer r = new StringBuffer()
        treeNode(iSaveImage, pack, d.contentDom.rootElement.childNodes, r)

        if (debug) println r

        r.toString()
    }

    String convertFromHtml(String html) {
        Document doc = Jsoup.parse(html)
        Elements newsHeadlines = doc.select("h1,h2,h3,h4,p,table,tr,td,li,ol,ul,br,b,i,span,font")
        StringBuffer asciidoc = new StringBuffer()
        Element table = null
        Iterator<TextNode> pWithStyle = null
        for (Element headline : newsHeadlines) {
            log.info(headline.class.simpleName + " " + headline.tag())
            switch (headline.tag()) {
                case 'p':
                    pWithStyle = null
                    if (headline.textNodes().empty) {
                        asciidoc.append("${headline.text()}")
                    } else {
                        pWithStyle = headline.textNodes().iterator()
                        asciidoc.append("${pWithStyle.next().text()}")
                    }
                    break
                case 'span':
                    asciidoc.append(headline.text())
                    if (pWithStyle?.hasNext()) asciidoc.append(pWithStyle.next().text())
                    break
                case 'font':
                    asciidoc.append(headline.text())
                    if (pWithStyle?.hasNext()) asciidoc.append(pWithStyle.next().text())
                    break
                case 'b':
                    asciidoc.append('**' + headline.text() + '**')
                    if (pWithStyle?.hasNext()) asciidoc.append(pWithStyle.next().text())
                    break
                case 'i':
                    asciidoc.append('_' + headline.text() + '_')
                    if (pWithStyle?.hasNext()) asciidoc.append(pWithStyle.next().text())
                    break
                case 'br':
                    if (!table) asciidoc.append('\n')
                    break
                case 'h1':
                    asciidoc.append("\n\n= ${headline.text()}\n")
                    break
                case 'h2':
                    asciidoc.append("\n\n== ${headline.text()}\n")
                    break
                case 'h3':
                    asciidoc.append("\n\n=== ${headline.text()}\n")
                    break
                case 'ul':
                    if (headline.parents().count { it.tag().toString() == 'ul'} == 0)
                        asciidoc.append("\n")
                    break
                case 'li':
                    asciidoc.append("\n${'*' * headline.parents().count { it.tag().toString() == 'ul'}}${'1.' * headline.parents().count { it.tag().toString() == 'ol'}} ")
                    break
                case 'h4':
                    asciidoc.append("\n==== ${headline.text()}\n")
                    break
                case 'table':
                    table = headline
                    asciidoc.append("\n|===")
                    break
                case 'tr':
                    asciidoc.append('\n')
                    break
                case 'td':
                    asciidoc.append("| ${headline.textNodes().empty ? '' : headline.textNodes()*.text().join(' ')}")
                    break
            }
            if (table && headline != table && !headline.parents().contains(table)) {
                asciidoc.append("\n|===\n")
                table = null
            }
        }
        if (table) asciidoc.append("\n|===\n")

        return asciidoc.toString()
    }

    String bodySlideshow(boolean controls, boolean progress, int autoSlide, int height, String content, Long id = null) {
        StringBuffer innerHtml = new StringBuffer(4096)
        innerHtml.append """
:revealjs_controls: ${controls}
:revealjs_progress: ${progress}
:revealjs_slidenumber: true
:revealjs_history: true
:revealjs_keyboard: true
:revealjs_overview: true
:revealjs_center: true
:revealjs_touch: true
:revealjs_loop: false
:revealjs_rtl: false
:revealjs_fragments: true
:revealjs_embedded: false
:revealjs_autoslide: ${autoSlide}
:revealjs_autoslidestoppable: true
:revealjs_mousewheel: true
:revealjs_hideaddressbar: true
:revealjs_previewlinks: false

:revealjs_transition: default
:revealjs_transitionspeed: default
:revealjs_backgroundtransition: default
:revealjs_viewdistance: 3
:revealjs_parallaxbackgroundimage:
:revealjs_parallaxbackgroundsize:
:revealjs_customtheme: reveal.js/css/theme/solarized.css
//:revealjs_customtheme: reveal.js/css/theme/solarized.css
:revealjs_theme: serif

:source-highlighter: highlightjs
:highlightjs-languages: groovy, gnuplot
:title-slide-transition: zoom
:title-slide-transition-speed: fast
:icons: font
:docinfo: shared
:customcss: custom.css
:revealjs_height: ${height}

"""
        innerHtml.append content
        AttributesBuilder attributes = Attributes.builder()
                .docType('article')
                .backend('revealjs')
                .title('Test')
        OptionsBuilder options = Options.builder()
                .safe(SafeMode.UNSAFE)
                .attributes(attributes.build())

//        Asciidoctor asciidoctor = Asciidoctor.Factory.create()
//        asciidoctor.requireLibrary('asciidoctor-diagram', 'asciidoctor-revealjs')
        def document = asciidoctor.load(innerHtml.toString(), options.build())
        String slideshowContent = document.convert()
//        asciidoctor.shutdown()

        """
            <div style="height: ${height}px;">
                <div class="reveal deck${id ?: 1}">
                    <div class='slides'>
                        ${slideshowContent}
                    </div>
                </div>
            </div>
<script postExecute='true'>
    // More info about initialization & config:
    // - https://revealjs.com/initialization/
    // - https://revealjs.com/config/
    if (typeof Reveal != 'undefined' && document.querySelector( '.deck${id ?: 1}' )) {
        let deck1 = Reveal(document.querySelector( '.deck${id ?: 1}' ), {
            embedded: true,
            keyboardCondition: 'focused' // only react to keys when focused
        })
        deck1.initialize({
            hash: false,
            fragments: true,
            fragmentInURL: false,
            loop: true,
            transition: 'default',
            transitionSpeed: 'default',
            backgroundTransition: 'default',
            viewDistance: 3,
            
            width: 960,
height: ${height},
controls: ${controls},
progress: ${progress},
autoSlide: ${autoSlide},

            plugins: [RevealHighlight, RevealZoom]
        });
    }
    
</script>
        """
    }
}