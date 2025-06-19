package taack.ui.dump.vt100

import groovy.transform.CompileStatic

import java.util.logging.Logger

/*
Todo: speed up with buffering
*/

@CompileStatic
final class DisplayManager {

    private static final Logger log = Logger.getLogger(DisplayManager.class.name)

    final static char escape = 27
    final char tab = 9

    final private char upArrow = 'A'
    final private char downArrow = 'B'
    final private char rightArrow = 'C'
    final private char leftArrow = 'D'
    final private char shiftTab = 'Z'

    final private String escapeReadOnly = "${escape}[38;7m"
    final private String escapeError = "${escape}[31m"
    final private String escapeEmphasis = "${escape}[1m"
    final private String escapeReset = "${escape}[0m"
    final private String escapeClear = "${escape}[2J"
    final private String escapeClearTillEndOfLine = "${escape}[0K"
    final private String escapeClearLine = "${escape}[2K"
    final private String escapeCursorToScreenEnds = "${escape}[0J"
    final private String escapeHighLight = "${escape}[1m"
    final private String escapeIsSortableStart = "${escape}[01;34m"

    final private InputStream ins
    final private OutputStream out

    final static String goToTopIcon = '⇱'
    final static String goToBottomIcon = '⇲'
    final static String refreshIcon = '⟳'
    final static String upIcon = '↑'
    final static String downIcon = '↓'
    final char cellScroll = '⇆'

    protected Pos curDim
    protected List<Area> areas = []

    private final Deque<Modal> modals = new ArrayDeque<>(8)
    private final Deque<Pos> screenHOffsetHistory = new ArrayDeque<>(32)
    private final Deque<Pos> screenVOffsetHistory = new ArrayDeque<>(32)

    private Pos drawingPos = new Pos(1, 1)

    private Area currentDrawArea = null
    private Area currentSelArea = null
    private AreaScrollMode currentScrollMode = null
    private int currentCellIndex = 0
    private int currentRegionIndex = 0
    private int screenOffsetCol = 0
    private int screenOffsetRow = 0
    private final Serializer serializer = new Serializer()
    final enum Browsing {
        UP, DOWN, LEFT, RIGHT, TAB, SH_TAB
    }

    final enum AreaScrollMode {
        NORMAL, H_LOCKED, V_LOCKED, HV_LOCKED
    }

    private class Serializer {
        final StringBuffer serial = new StringBuffer(128)
        boolean spooledMode = false

        void write(String s) {
            serial.append(s)
        }

        void writeCommand(String s) {
            if (spooledMode) write(s)
            else {
                out << serial.toString()
                if (s) out << s
                serial.delete(0, serial.length());
            }
        }
    }

    private class Deco {
        final String content
        final Pos pos

        Deco(String content, Pos pos) {
            this.content = content
            this.pos = pos
        }

        int getLen() {
            content.length()
        }

        protected String padString(int len = content.length()) {
            if (len < 0) throw new IllegalArgumentException("len($len) < 0")
            if (len == content.length()) content
            else if (content.length() < len) content + ' ' * (len - content.length())
            else if (content.length() > len && len > 1) {
                content.substring(0, len - 1) + '<'
            } else '<'
        }

        void draw() {
            if (outside) return
            pos.moveTo()
            if (!outside && !onboundary) {
                serializer.write content
                drawingPos = drawingPos + len
            } else if (onboundary) {
                serializer.write padString(curDim.col + screenOffsetCol - pos.col)
                drawingPos = drawingPos + (curDim.col + screenOffsetCol - pos.col)
            }
        }

        boolean isOutside() {
            pos.outside
        }

        boolean isOnboundary() {
            !outside && (pos + content.length()).outside
        }
    }

    private final class Cell extends Deco {

        Cell(String content, Pos pos, int len, boolean padLeft = true) {
            super(content, pos)
            this.len = len
            this.padLeft = padLeft
        }

        boolean padLeft
        int len

        String padString(int pLen = len) {
            if (pLen < 0) throw new IllegalArgumentException('len < 0')
            String s = content
            if (pLen == s.length()) s
            else if (s.length() < pLen) s + ' ' * (pLen - s.length())
            else if (s.length() > pLen && pLen > 1) {
                s.substring(0, pLen - 1) + cellScroll
            } else cellScroll
        }

        void draw() {
            if (outside) return
            pos.moveTo()
            if (!outside && !onboundary) {
                serializer.write padString()
            } else if (onboundary) {
                serializer.write padString(curDim.col + screenOffsetCol - pos.col)
            }
            drawingPos = drawingPos + len
        }

        boolean isOnboundary() {
            !outside && (pos + len).outside
        }

        void highlight() {
            serializer.writeCommand escapeHighLight
            draw()
            serializer.writeCommand escapeReset
        }
    }

    final class Pos {
        final int row
        final int col

        Pos(int row, int col) {
            this.col = col
            this.row = row
        }

        Pos plus(Pos p) {
            new Pos(row + p.row, col + p.col)
        }

        Pos plus(int x) {
            new Pos(row, col + x)
        }

        Pos nextLine(int y) {
            new Pos(row + y, 1)
        }

        void moveTo() {
            Pos areaPos = offsetAreaPos()
            if (drawingPos.row == areaPos.row && drawingPos.col == areaPos.col) return
            moveCursor(areaPos.row, areaPos.col)
            drawingPos = areaPos
        }

        boolean isOutside() {
            if (currentDrawArea.scrollMode == AreaScrollMode.V_LOCKED) {
                row > curDim.row ||
                        col > curDim.col + screenOffsetCol ||
                        col < screenOffsetCol
            } else {
                Pos areaPos = this //offsetAreaPos()
                areaPos.row > curDim.row + screenOffsetRow ||
                        areaPos.row < screenOffsetRow ||
                        areaPos.col > curDim.col + screenOffsetCol ||
                        areaPos.col < screenOffsetCol
            }
        }

        @Override
        String toString() {
            "Pos($row x $col)"
        }

        final Pos offsetAreaPos() {
            if (screenOffsetRow == 0 && screenOffsetCol == 0)
                this
            else {
                if (currentDrawArea.scrollMode == AreaScrollMode.V_LOCKED) {
                    this
                } else {
                    if (screenOffsetRow == 0 && screenOffsetCol != 0)
                        this + new Pos(0, currentDrawArea.areaColLeft)
                    else if (screenOffsetRow != 0 && screenOffsetCol == 0)
                        this + new Pos(currentDrawArea.areaRowTop, 0)
                    else if (screenOffsetRow != 0 && screenOffsetCol != 0)
                        this + new Pos(currentDrawArea.areaRowTop, currentDrawArea.areaColLeft)
                }
            }
        }
    }

    private class Area {
        Area(AreaScrollMode scrollMode) {
            this.scrollMode = scrollMode
        }
        final AreaScrollMode scrollMode
        final List<Deco> toDraw = []
        private List<Cell> hiddenCells = null
        private Integer rowTop = null
        private Integer rowBottom = null
        private Integer colOffset = null

        final List<Cell> getCells() {
            hiddenCells = hiddenCells ?: toDraw.findAll { it instanceof Cell } as List<Cell>
        }

        final int getCellIndexBelow() {
            final Cell c = cells[currentCellIndex]
            int ci = currentCellIndex
            cells[ci..(cells.size() - 1)].find { Cell entry ->
                ci++
                entry.pos.col >= c.pos.col && entry.pos.row > c.pos.row
            }
            --ci
        }

        final int getCellIndexBelow(Area other) {
            final Cell c = cells[currentCellIndex]
            int ci = 0
            other.cells[ci..(other.cells.size() - 1)].find { Cell entry ->
                entry.pos.col >= c.pos.col || ci++
            }
            ci
        }

        final int getCellIndexAbove() {
            final Cell c = cells[currentCellIndex]
            int ci = currentCellIndex
            cells[ci..0].find { Cell entry ->
                ci--
                entry.pos.col <= c.pos.col && entry.pos.row < c.pos.row
            }
            ++ci
        }

        final int getCellIndexAbove(Area other) {
            final Cell c = cells[currentCellIndex]
            int ci = other.cells.size() - 1
            other.cells[ci..0].find { Cell entry ->
                ci--
                entry.pos.col <= c.pos.col && entry.pos.row < c.pos.row
            }
            ++ci
        }

        final int getAreaRowTop() {
            rowTop = rowTop ?: toDraw.first().pos.row
        }

        final int getAreaRowBottom() {
            rowBottom = rowBottom ?: toDraw.last().pos.row
            Math.min(curDim.row, rowBottom)
        }

        final int getAreaColLeft() {
            colOffset = colOffset ?: toDraw.first().pos.col
        }

        private final void clear() { // Does not work ...
            for (i in (areaRowTop..areaRowBottom)) {
                moveCursor(i, 1)
                serializer.writeCommand escapeClearLine
            }
        }

        final void draw(AreaScrollMode mode) {
            currentDrawArea = this
            toDraw.each {
                it.draw()
            }
        }
    }

    class Modal {
        protected Pos curDim
        protected List<Area> regions
    }

    DisplayManager(InputStream inputStream, OutputStream outputStream) {
        this.ins = inputStream
        this.out = outputStream
        log.info 'query dim'
        curDim = dimensions
        log.info "create new DisplayManager with original dimension ${curDim.col} x ${curDim.row}"
    }

    private void saveCursorPos() {
        serializer.writeCommand "$escape[s"
    }

    private void clearScreen() {
        serializer.writeCommand escapeClear
    }

    private void restoreCursorPos() {
        serializer.writeCommand "$escape[u"
    }

    private String readInputUntil(char until) {
        StringBuffer response = new StringBuffer(32)
        char c = ins.read()
        while (c != until) {
            c = ins.read()
            response.append(c)
        }
        response.toString()
    }

    private void queryCursorPos() {
        serializer.writeCommand "$escape[6n"
    }

    private void moveCursor(int row, int col) {
        if (currentDrawArea && currentDrawArea.scrollMode == AreaScrollMode.V_LOCKED)
            serializer.writeCommand "$escape[${row};${col - screenOffsetCol}H"
        else
            serializer.writeCommand "$escape[${row - screenOffsetRow};${col - screenOffsetCol}H"

    }

    private Pos getCursorPosition() {
        queryCursorPos()
        String reply = readInputUntil((char) 'R')
        def dim = reply.toString() =~ /\[([0-9]+);([0-9]+)R/
        dim.matches()
        int row = dim.group(1).toInteger()
        int col = dim.group(2).toInteger()
        new Pos(row, col)
    }

    private Pos getDimensions() {
        saveCursorPos()
        moveCursor(999, 999)
        Pos pos = cursorPosition
        restoreCursorPos()
        pos
    }

    Browsing waitForBrowsingInput() {
        char c = ins.read()
        if (c == tab) {
            return Browsing.TAB
        } else if (c == escape) {
            c = ins.read()
            if (c == '[' as char) {
                c = ins.read()
                if (c == shiftTab) return Browsing.SH_TAB
                else if (c == upArrow) return Browsing.UP
                else if (c == downArrow) return Browsing.DOWN
                else if (c == leftArrow) return Browsing.LEFT
                else if (c == rightArrow) return Browsing.RIGHT
            } else return null
        } else return null
        return null
    }

    void newRegion(AreaScrollMode scrollMode) {
        areas.add(new Area(scrollMode))
    }

    void addFocusableCell(String cell, int len, boolean padLeft = true) {
        areas.last().toDraw.add new Cell(cell, drawingPos, len, padLeft)
        drawingPos = drawingPos + len
    }

    void gotoNextLine() {
        drawingPos = drawingPos.nextLine(1)
    }

    void addDecoration(String deco) {
        areas.last().toDraw.add new Deco(deco, drawingPos)
        drawingPos = drawingPos + deco.length()
    }

    void draw(AreaScrollMode scrollMode = AreaScrollMode.NORMAL) {
//        drawingPos = new Pos(1, 1)
//        moveCursor(1, 1)
        currentScrollMode = scrollMode

        serializer.spooledMode = true
        clearScreen()
        moveCursor(1, 1)
        for (Area r in areas) {
            r.draw(scrollMode)
        }
        serializer.spooledMode = false
        serializer.writeCommand(null)
    }

    void nav() {
        if (!currentSelArea) {
            moveCursor(1, 1)
            currentSelArea = areas.first()
            currentCellIndex = 0
        }
        while (true) {
            switch (waitForBrowsingInput()) {
                case Browsing.RIGHT:
                    if (currentCellIndex < currentSelArea.cells.size() - 1) {
                        Cell oc = currentSelArea.cells[currentCellIndex]
                        currentCellIndex++
                        Cell c = currentSelArea.cells[currentCellIndex]
                        if (c.pos.col < oc.pos.col) {
                            currentCellIndex--
                            continue
                        }
                        if (c.onboundary || c.outside) {
                            screenHOffsetHistory.push(new Pos(0, c.pos.col))
                            screenOffsetCol += c.pos.col
                            draw(AreaScrollMode.H_LOCKED)
                        }
                        oc.draw()
                        c.highlight()
                    }
                    break
                case Browsing.LEFT:
                    if (currentCellIndex > 0) {
                        Cell oc = currentSelArea.cells[currentCellIndex]
                        currentCellIndex--
                        Cell c = currentSelArea.cells[currentCellIndex]
                        if (c.pos.col > oc.pos.col) {
                            currentCellIndex++
                            continue
                        }

                        if (c.onboundary || c.outside) {
                            screenOffsetCol -= (screenHOffsetHistory.peek().col)
                            draw(AreaScrollMode.H_LOCKED)
                        }
                        oc.draw()
                        c.highlight()
                    }
                    break
                case Browsing.UP:
                    Cell oc = currentSelArea.cells[currentCellIndex]
                    int ni = currentSelArea.cellIndexAbove
                    Cell c = currentSelArea.cells[ni]
                    if (c.pos.row > oc.pos.row && c.pos.col == oc.pos.col) {
                        continue
                    }
                    currentCellIndex = ni
                    int startPos = currentSelArea.areaRowTop
                    Pos aPos = c.pos
                    if (c.onboundary || aPos.outside) {
                        screenOffsetRow -= (screenVOffsetHistory.peek().row)
                        draw(AreaScrollMode.V_LOCKED)
                    }
                    oc.draw()
                    c.highlight()
                    break
                case Browsing.DOWN:
                    Cell oc = currentSelArea.cells[currentCellIndex]
                    int ni = currentSelArea.cellIndexBelow
                    Cell c = currentSelArea.cells[ni]
                    if (c.pos.row <= oc.pos.row && c.pos.col != oc.pos.col) {
                        continue
                    }
                    currentCellIndex = ni
                    int startPos = currentSelArea.areaRowTop
                    Pos aPos = c.pos
                    if (c.onboundary || aPos.outside) {
                        screenVOffsetHistory.push(new Pos(c.pos.row, 0))
                        screenOffsetRow += c.pos.row
                        draw(AreaScrollMode.V_LOCKED)
                    }
                    oc.draw()
                    c.highlight()
                    break
                case Browsing.TAB:
                    if (currentRegionIndex >= areas.size() - 1) continue
                    Cell c = currentSelArea.cells[currentCellIndex]
                    c.draw()
                    Area other = areas[++currentRegionIndex]
                    if (!other) return
                    int newIndex = currentSelArea.getCellIndexBelow(other)
                    currentSelArea = other
                    currentCellIndex = newIndex
                    currentSelArea.cells[currentCellIndex].highlight()
                    break
                case Browsing.SH_TAB:
                    if (currentRegionIndex == 0) continue
                    Cell c = currentSelArea.cells[currentCellIndex]
                    c.draw()
                    Area other = areas[--currentRegionIndex]
                    if (!other) return
                    int newIndex = currentSelArea.getCellIndexAbove(other)
                    currentSelArea = other
                    currentCellIndex = newIndex
                    currentSelArea.cells[currentCellIndex].highlight()
                    break
                default:
                    Cell c = currentSelArea.cells[currentCellIndex]
                    moveCursor(c.pos.row + 1, 1)
                    serializer.writeCommand(escapeCursorToScreenEnds)
                    return
            }
        }
    }
}
