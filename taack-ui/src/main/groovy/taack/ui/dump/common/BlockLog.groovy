package taack.ui.dump.common

import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.theme.ThemeSelector

final class BlockLog {
    private final String indent = '   '
    private int occ = 0
    final static boolean debug = false
    IHTMLElement topElement
    final ThemeSelector ts

    BlockLog(final ThemeSelector ts) {
        this.ts = ts
    }

    void enterBlock(String method) {
        if (debug) {
            if (occ < 0) println "OCC < 0 !!! occ == $occ " + method + ' +++ ' + topElement
            else println(indent*occ++ + method + ' +++ ' + topElement)
        }
    }

    void stayBlock(String method) {
        if (debug) {
            if (occ < 0) println "OCC < 0 !!! occ == $occ " + method + ' === ' + topElement
            else println(indent*occ + method + ' === ' + topElement)
        }
    }
    void simpleLog(String method) {
        if (debug) {
            if (occ < 0) println "OCC < 0 !!! occ == $occ " + method
            else println(indent*occ + method)
        }
    }

    void exitBlock(String method) {
        if (debug) {
            if (occ < 0) {
                println "OCC < 0 !!! occ == $occ " + method + ' --- ' + topElement
            } else {
                println(indent*--occ + method + ' --- ' + topElement)
            }
        }
    }

}
