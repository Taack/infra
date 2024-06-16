package taack.ui.dump.common

import taack.ui.dump.html.element.IHTMLElement

final class BlockLog {
    private final String indent = '   '
    private int occ = 0
    final static boolean debug = true
    IHTMLElement topElement

    void enterBlock(String method) {
        if (debug) println(indent*occ++ + method + ' +++ ' + topElement)
    }

    void stayBlock(String method) {
        if (debug) {
            if (occ <= 0) println "OCC <= 0 !!! occ == $occ"
            println(indent*occ + method + ' === ' + topElement)
        }
    }

    void exitBlock(String method) {
        if (debug) {
            if (occ <= 0) println "OCC <= 0 !!! occ == $occ"
            println(indent*--occ + method + ' --- ' + topElement)
        }
    }

}
