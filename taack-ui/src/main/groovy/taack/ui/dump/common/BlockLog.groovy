package taack.ui.dump.common

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.theme.ThemeSelector

@CompileStatic
final class BlockLog {
    private final String indent = '   '
    private int occ = 0
    final static boolean debug = false
    IHTMLElement topElement
    final ThemeSelector ts

    private final Deque<IHTMLElement> positionStack = new ArrayDeque<>()

    void savePosition() { positionStack.push(topElement) }
    IHTMLElement peekPosition() { positionStack.peek() }
    void restorePosition() { topElement = positionStack.pop() }

    BlockLog(final ThemeSelector ts) {
        this.ts = ts
    }

    void logEnterBlock(String method) {
        if (debug) {
            if (occ < 0) println "OCC < 0 !!! occ == $occ " + method + ' +++ ' + topElement
            else println(indent*occ++ + method + ' +++ ' + topElement)
        }
    }

    void logStayBlock(String method) {
        if (debug) {
            if (occ < 0) println "OCC < 0 !!! occ == $occ " + method + ' === ' + topElement
            else println(indent*occ + method + ' === ' + topElement)
        }
    }
    void simpleLog(String method, boolean force = false) {
        if (debug || force) {
            if (occ < 0) println "OCC < 0 !!! occ == $occ " + method
            else println(indent*occ + method)
        }
    }

    void logExitBlock(String method) {
        if (debug) {
            if (occ < 0) {
                println "OCC < 0 !!! occ == $occ " + method + ' --- ' + topElement
            } else {
                println(indent*--occ + method + ' --- ' + topElement)
            }
        }
    }

}
