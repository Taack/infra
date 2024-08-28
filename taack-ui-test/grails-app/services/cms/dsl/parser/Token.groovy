package cms.dsl.parser

import groovy.transform.CompileStatic

import java.util.regex.Pattern

/**
 * IMG(#12)
 * VID(#24)
 * URL(#23)
 * LINK(#23)
 * LINK("http://machin.com/url?toto=true&titi=false")
 * LINK_CLOSE
 */
@CompileStatic
enum Token {
    EPSILON("xXEPSILONXx"),
    ERROR("xXERRORXx"),
    IMG_LINK("IMG_LINK"),
    IMG("IMG"),
    CLOSE_LINK("CLOSE_LINK"),
    ITEM_LINK("ITEM_LINK"),
    LINK("LINK"),
    URL("URL"),
    INSERT_LINK("INSERT_LINK"),
    VID_LINK("VID_LINK"),
    SLIDESHOW("SLIDESHOW"),
    VID("VID"),
    PDF("PDF"),
    ID("#[0-9]{1,}"),
    CLASS("CLASS:"),
    ALIGN_LEFT("ALIGN_LEFT"),
    ALIGN_RIGHT("ALIGN_RIGHT"),
    STYLE("STYLE:"),
    PEEK_ARG_SEP(","),
    URL_LITERAL("https?://[a-z\\?\\&=/]"),
    CLASS_LIST("[\"']'[a-zA-Z0-9_\\- ]*[\"']"),
    STYLE_LITERAL("[\"'][a-z\\%0-9_\\-:; ]*[\"']")

    Token(String regex) {
        this.regex = Pattern.compile("^(" + regex + ")")
    }

    final Pattern regex

}
