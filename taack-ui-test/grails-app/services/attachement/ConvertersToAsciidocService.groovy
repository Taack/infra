package attachement

import grails.compiler.GrailsCompileStatic
import org.apache.xerces.dom.TextImpl
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
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
import org.w3c.dom.Node
import org.w3c.dom.NodeList

@GrailsCompileStatic
class ConvertersToAsciidocService {

    final private boolean debug = false

    static String traverseNodeBefore(Node node, Node... parents) {
        if (node instanceof OdfDrawImage) {
            boolean softBreakAfter = (parents.findAll { it instanceof OdfTextParagraph }?.last()?.childNodes?.find { it instanceof TextSoftPageBreakElement }) != null
            return (softBreakAfter ? '' : '\n') + 'image:' + (softBreakAfter ? '' : ':') + node.imageUri + '[]' + (softBreakAfter ? ' ' : '\n')
        } else if (node instanceof OdfTextParagraph) {
            int numberOfParent = (int) parents.length
            return numberOfParent == 3 ? '\n' : ''
        } else if (node instanceof TextImpl) {
            return node.textContent
        } else if (node instanceof OdfTextSpan) {
            String s = node.attributes.getNamedItem('style-name')
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
            String s = node.attributes.getNamedItem('style-name')
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

    void treeNode(NodeList entry, StringBuffer asciidoc, Node... parents) {
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
                    asciidoc.append('\n')
                    OdfDrawFrame frame = n.childNodes.find { it instanceof OdfDrawFrame } as OdfDrawFrame
                    if (frame) {
                        asciidoc.append(traverseNodeBefore(frame, parents + frame))
                        treeNode(frame.childNodes, asciidoc, parents + frame)
                        asciidoc.append(traverseNodeAfter(frame, parents + frame))
                    }
                } else {
                    asciidoc.append(traverseNodeBefore(n, parents + n))
                    treeNode(n.childNodes, asciidoc, parents + n)
                    asciidoc.append(traverseNodeAfter(n, parents + n))
                }
            } else {
                asciidoc.append(traverseNodeBefore(n, parents + n))
            }
        }
    }

    String convert(File file) {
        OdfDocument d = OdfDocument.loadDocument(file)
        StringBuffer r = new StringBuffer()
        treeNode(d.contentDom.rootElement.childNodes, r)

        if (debug) println r

        r.toString()
    }

    String convertFromHtml(String html) {
        Document doc = Jsoup.parse(html)
        Elements newsHeadlines = doc.select("h1,h2,h3,h4,p,table,tr,td")
        StringBuffer asciidoc = new StringBuffer()
        Element table = null
        for (Element headline : newsHeadlines) {
            log.info(headline.class.simpleName + " " + headline.tag())
            switch (headline.tag()) {
                case 'p':
                    asciidoc.append("\n${headline.text()}\n")
                    break
                case 'h1':
                    asciidoc.append("\n= ${headline.text()}\n")
                    break
                case 'h2':
                    asciidoc.append("\n== ${headline.text()}\n")
                    break
                case 'h3':
                    asciidoc.append("\n=== ${headline.text()}\n")
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
                    asciidoc.append("| ${headline.text()}")
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
