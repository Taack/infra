package attachement

import cms.CmsController
import cms.CmsImage
import cms.CmsPage
import crew.User
import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.xerces.dom.TextImpl
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
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import taack.domain.TaackAttachmentService

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

    final private boolean debug = true

    String traverseNodeBefore(CmsPage page, OdfPackage odfPackage, Node node, Node... parents) {
        if (node instanceof OdfDrawImage) {

            byte[] imageBytes = odfPackage.getBytes(node.imageUri.path)
            CmsImage cmsImage = saveImage(page, node.imageUri.path, imageBytes)

            boolean softBreakAfter = (parents.findAll { it instanceof OdfTextParagraph }?.last()?.childNodes?.find { it instanceof TextSoftPageBreakElement }) != null
            return (softBreakAfter ? '' : '\n') + 'image:' + (softBreakAfter ? '' : ':') + cmsImage.originalName + '[]' + (softBreakAfter ? ' ' : '\n')
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
            return '\n|===='
        } else if (node instanceof TableTableColumnElement) {
        } else if (node instanceof TableTableRowElement) {
            return '\n'
        } else if (node instanceof TableTableCellElement) {
            return '| '
        }
        return ''
    }

    static String traverseNodeAfter(Node node, Node... parents) {
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
            return '\n|====\n'
        }
        ''
    }

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

    void treeNode(CmsPage page, OdfPackage odfPackage, NodeList entry, StringBuffer asciidoc, Node... parents) {
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
                        asciidoc.append(traverseNodeBefore(page, odfPackage, frame, parents + frame))
                        treeNode(page, odfPackage, frame.childNodes, asciidoc, parents + frame)
                        asciidoc.append(traverseNodeAfter(frame, parents + frame))
                    }
                } else {
                    asciidoc.append(traverseNodeBefore(page, odfPackage, n, parents + n))
                    treeNode(page, odfPackage, n.childNodes, asciidoc, parents + n)
                    asciidoc.append(traverseNodeAfter(n, parents + n))
                }
            } else {
                asciidoc.append(traverseNodeBefore(page, odfPackage, n, parents + n))
            }
        }
    }

    String convert(CmsPage page, InputStream inputStream) {
        OdfDocument d = OdfDocument.loadDocument(inputStream)
        OdfPackage pack = d.package
        StringBuffer r = new StringBuffer()
        treeNode(page, pack, d.contentDom.rootElement.childNodes, r)

        if (debug) println r

        r.toString()
    }

    String convertFromHtml(String html) {
        Document doc = Jsoup.parse(html)
        Elements newsHeadlines = doc.select("h1,h2,h3,h4,p,table,tr,td,li,ol,ul,br,b,i,span")
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
                case 'b':
                    asciidoc.append('**' + headline.text() + '**')
                    if (pWithStyle?.hasNext()) asciidoc.append(pWithStyle.next().text())
                    break
                case 'i':
                    asciidoc.append('_' + headline.text() + '_')
                    if (pWithStyle?.hasNext()) asciidoc.append(pWithStyle.next().text())
                    break
                case 'br':
                    asciidoc.append('\n')
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
                    asciidoc.append("| ")
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
}
