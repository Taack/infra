package taack.wysiwyg

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class MarkdownCustomMenuButton implements Serializable {
    String title
    String icon
    String openModalUrl
}
