package taack.ui.dump.diagram.scene

import groovy.transform.CompileStatic
import taack.ui.dsl.diagram.DiagramOption
import taack.ui.dump.diagram.IDiagramRender
import taack.ui.dump.diagram.PngDiagramRender
import taack.ui.dump.diagram.SvgDiagramRender

import java.awt.Color

@CompileStatic
final class OrgChartNode {
    final String key
    final String parentKey
    final String title
    final String subtitle
    final String header
    final String image

    OrgChartNode(String key, String parentKey, String title, String subtitle, String header, String image) {
        this.key = key
        this.parentKey = parentKey
        this.title = title ?: ''
        this.subtitle = subtitle
        this.header = header
        this.image = image
    }
}

/**
 * Draws a forest of boxes, each showing from top to bottom its image, header, title and subtitle, centered.
 * The image and header rows exist in every box as soon as one node has them, so all boxes share the same size.
 * A node whose children are all leaves stacks them vertically below itself;
 * otherwise its children are laid out side by side, its leaves grouped in a single stacked column.
 * Leaves are split in balanced columns of at most MAX_STACK_LENGTH boxes.
 * Children columns wrap on several rows linked by a trunk once a row exceeds MAX_ROW_WIDTH,
 * which keeps wide teams printable. With a click action, clicking a box sends its key as the x parameter. The canvas is sized to the content, so the diagram resolution only sets the scale.
 */
@CompileStatic
class OrgChartDiagramScene extends DiagramScene {
    private static final BigDecimal PNG_SCALE = 2.0
    private static final String ELLIPSIS = '…'
    private static final int MAX_STACK_LENGTH = 6

    private BigDecimal CHART_MARGIN = 20.0
    private BigDecimal BOX_PADDING = 6.0
    private BigDecimal BOX_ACCENT_THICKNESS = 4.0
    private BigDecimal LINE_GAP = 3.0
    private BigDecimal MIN_BOX_WIDTH = 120.0
    private BigDecimal MAX_BOX_WIDTH = 220.0
    private BigDecimal HORIZONTAL_GAP = 20.0
    private BigDecimal VERTICAL_GAP = 30.0
    private BigDecimal STACK_GAP = 8.0
    private BigDecimal STACK_INDENT = 16.0
    private BigDecimal TRUNK_MARGIN = 20.0
    private BigDecimal MAX_ROW_WIDTH = 1600.0
    private BigDecimal IMAGE_SIZE = 40.0

    final private Color BOX_COLOR = new Color(255, 255, 255)
    final private Color LINK_COLOR = new Color(150, 150, 150)
    final private Color IMAGE_PLACEHOLDER_COLOR = new Color(225, 225, 225)

    final private List<OrgChartNode> roots
    final private Map<String, List<OrgChartNode>> childrenByKey
    final private BigDecimal boxWidth
    final private BigDecimal boxHeight
    final private boolean hasImageRow
    final private boolean hasHeaderRow
    final private Map<OrgChartNode, BigDecimal> subtreeWidths = [:]
    final private Map<OrgChartNode, BigDecimal> subtreeHeights = [:]

    /**
     * Either the stacked leaves of a node, or one of its branch children with its whole subtree.
     */
    @CompileStatic
    private static final class ChildColumn {
        final List<OrgChartNode> leaves
        final OrgChartNode branch
        final BigDecimal width
        final BigDecimal height

        ChildColumn(List<OrgChartNode> leaves, OrgChartNode branch, BigDecimal width, BigDecimal height) {
            this.leaves = leaves
            this.branch = branch
            this.width = width
            this.height = height
        }
    }

    static OrgChartDiagramScene create(boolean isPng, boolean isViewBox, List<OrgChartNode> nodes, DiagramOption diagramOption) {
        BigDecimal scale = diagramOption?.resolution?.fontSizePercentage ?: (isPng ? PNG_SCALE : 1.0)
        Map<String, OrgChartNode> nodeByKey = nodes.collectEntries { [it.key, it] }
        List<OrgChartNode> roots = nodes.findAll { !it.parentKey || it.parentKey == it.key || !nodeByKey.containsKey(it.parentKey) }
        Map<String, List<OrgChartNode>> childrenByKey = nodes.findAll { !roots.contains(it) }.groupBy { it.parentKey }

        OrgChartDiagramScene measuringScene = new OrgChartDiagramScene(new SvgDiagramRender(1.0, 1.0, false, scale), roots, childrenByKey, diagramOption, scale)
        BigDecimal width = measuringScene.chartWidth()
        BigDecimal height = measuringScene.chartHeight()
        IDiagramRender render = isPng ? new PngDiagramRender(width, height, scale) : new SvgDiagramRender(width, height, isViewBox, scale)
        return new OrgChartDiagramScene(render, roots, childrenByKey, diagramOption, scale)
    }

    private OrgChartDiagramScene(IDiagramRender render, List<OrgChartNode> roots, Map<String, List<OrgChartNode>> childrenByKey, DiagramOption diagramOption, BigDecimal scale) {
        super(render, diagramOption)
        this.roots = roots
        this.childrenByKey = childrenByKey

        CHART_MARGIN *= scale
        BOX_PADDING *= scale
        BOX_ACCENT_THICKNESS *= scale
        LINE_GAP *= scale
        MIN_BOX_WIDTH *= scale
        MAX_BOX_WIDTH *= scale
        HORIZONTAL_GAP *= scale
        VERTICAL_GAP *= scale
        STACK_GAP *= scale
        STACK_INDENT *= scale
        TRUNK_MARGIN *= scale
        MAX_ROW_WIDTH *= scale
        IMAGE_SIZE *= scale
        if (!diagramOption?.resolution) TITLE_MARGIN *= scale

        List<OrgChartNode> allNodes = roots + (childrenByKey.values().flatten() as List<OrgChartNode>)
        BigDecimal widestText = allNodes.collect { OrgChartNode n ->
            [render.measureText(n.title), n.subtitle ? render.measureSmallText(n.subtitle) : 0.0, n.header ? render.measureSmallText(n.header) : 0.0].max()
        }.max() ?: 0.0
        BigDecimal wantedWidth = widestText + BOX_PADDING * 2
        this.boxWidth = [[wantedWidth, MIN_BOX_WIDTH].max(), MAX_BOX_WIDTH].min()
        this.hasImageRow = allNodes.any { it.image }
        this.hasHeaderRow = allNodes.any { it.header }
        boolean hasSubtitleRow = allNodes.any { it.subtitle }
        this.boxHeight = BOX_ACCENT_THICKNESS + BOX_PADDING * 2 + fontSize +
                (hasImageRow ? IMAGE_SIZE + LINE_GAP : 0.0) +
                (hasHeaderRow ? smallFontSize() + LINE_GAP : 0.0) +
                (hasSubtitleRow ? LINE_GAP + smallFontSize() : 0.0)
    }

    private BigDecimal smallFontSize() {
        (fontSize * IDiagramRender.SMALL_LABEL_RATE).toInteger()
    }

    private List<OrgChartNode> children(OrgChartNode n) {
        childrenByKey[n.key] ?: []
    }

    private boolean isLeaf(OrgChartNode n) {
        children(n).isEmpty()
    }

    private List<OrgChartNode> leafChildren(OrgChartNode n) {
        children(n).findAll { isLeaf(it) }
    }

    private List<OrgChartNode> branchChildren(OrgChartNode n) {
        children(n).findAll { !isLeaf(it) }
    }

    private boolean stacksChildren(OrgChartNode n) {
        !isLeaf(n) && branchChildren(n).isEmpty() && children(n).size() <= MAX_STACK_LENGTH
    }

    private static List<List<OrgChartNode>> leafStacks(List<OrgChartNode> leaves) {
        if (leaves.isEmpty()) return []
        int stackCount = Math.ceil(leaves.size() / MAX_STACK_LENGTH).toInteger()
        leaves.collate(Math.ceil(leaves.size() / stackCount).toInteger())
    }

    private BigDecimal stackHeight(int count) {
        count * (boxHeight + STACK_GAP) - STACK_GAP
    }

    private BigDecimal stackWidth() {
        STACK_INDENT + boxWidth
    }

    private BigDecimal titleHeight() {
        diagramOption?.title ? TITLE_MARGIN + (fontSize * IDiagramRender.EMPHASIZED_LABEL_RATE).toInteger() + TITLE_MARGIN / 2 : 0.0
    }

    private List<ChildColumn> childColumns(OrgChartNode n) {
        List<ChildColumn> columns = []
        for (List<OrgChartNode> leaves : leafStacks(leafChildren(n))) {
            columns.add(new ChildColumn(leaves, null, stackWidth(), stackHeight(leaves.size())))
        }
        for (OrgChartNode branch : branchChildren(n)) {
            columns.add(new ChildColumn(null, branch, subtreeWidth(branch), subtreeHeight(branch)))
        }
        columns
    }

    private List<List<ChildColumn>> childRows(OrgChartNode n) {
        List<List<ChildColumn>> rows = [[]]
        BigDecimal currentRowWidth = 0.0
        for (ChildColumn column : childColumns(n)) {
            List<ChildColumn> row = rows.last()
            BigDecimal widthWithColumn = row.isEmpty() ? column.width : currentRowWidth + HORIZONTAL_GAP + column.width
            if (!row.isEmpty() && widthWithColumn > MAX_ROW_WIDTH) {
                rows.add([column])
                currentRowWidth = column.width
            } else {
                row.add(column)
                currentRowWidth = widthWithColumn
            }
        }
        rows
    }

    private BigDecimal rowWidth(List<ChildColumn> row) {
        (row*.width.sum() as BigDecimal) + HORIZONTAL_GAP * (row.size() - 1)
    }

    private static BigDecimal rowHeight(List<ChildColumn> row) {
        row*.height.max()
    }

    private BigDecimal childrenBlockWidth(List<List<ChildColumn>> rows) {
        BigDecimal widestRow = rows.collect { rowWidth(it) }.max()
        rows.size() > 1 ? TRUNK_MARGIN + widestRow : widestRow
    }

    private BigDecimal subtreeWidth(OrgChartNode n) {
        if (isLeaf(n)) return boxWidth
        if (stacksChildren(n)) return stackWidth()
        if (!subtreeWidths.containsKey(n)) subtreeWidths[n] = [boxWidth, childrenBlockWidth(childRows(n))].max()
        subtreeWidths[n]
    }

    private BigDecimal subtreeHeight(OrgChartNode n) {
        if (isLeaf(n)) return boxHeight
        if (stacksChildren(n)) return boxHeight + STACK_GAP + stackHeight(children(n).size())
        if (!subtreeHeights.containsKey(n)) {
            subtreeHeights[n] = boxHeight + (childRows(n).collect { VERTICAL_GAP + rowHeight(it) }.sum() as BigDecimal)
        }
        subtreeHeights[n]
    }

    private BigDecimal boxCenterOffset(OrgChartNode n) {
        isLeaf(n) || stacksChildren(n) ? boxWidth / 2 : subtreeWidth(n) / 2
    }

    private List<OrgChartNode> branchRoots() {
        roots.findAll { !isLeaf(it) }
    }

    private List<OrgChartNode> leafRoots() {
        roots.findAll { isLeaf(it) }
    }

    private BigDecimal forestWidth() {
        List<BigDecimal> widths = branchRoots().collect { subtreeWidth(it) }
        leafStacks(leafRoots()).each { widths.add(boxWidth) }
        widths.isEmpty() ? 0.0 : (widths.sum() as BigDecimal) + HORIZONTAL_GAP * (widths.size() - 1)
    }

    private BigDecimal forestHeight() {
        List<BigDecimal> heights = branchRoots().collect { subtreeHeight(it) }
        leafStacks(leafRoots()).each { heights.add(stackHeight(it.size())) }
        heights.max() ?: 0.0
    }

    BigDecimal chartWidth() {
        BigDecimal titleWidth = diagramOption?.title ? render.measureEmphasizedText(diagramOption.title) : 0.0
        [forestWidth(), titleWidth].max() + CHART_MARGIN * 2
    }

    BigDecimal chartHeight() {
        titleHeight() + forestHeight() + CHART_MARGIN * 2
    }

    private String fitText(String text, BigDecimal maxWidth, boolean isSmall) {
        Closure<BigDecimal> measure = { String t -> isSmall ? render.measureSmallText(t) : render.measureText(t) }
        if (measure(text) <= maxWidth) return text
        String fitted = text
        while (fitted.length() > 1 && measure(fitted + ELLIPSIS) > maxWidth) {
            fitted = fitted.substring(0, fitted.length() - 1)
        }
        fitted + ELLIPSIS
    }

    private void drawLine(BigDecimal fromX, BigDecimal fromY, BigDecimal toX, BigDecimal toY) {
        render.fillStyle(LINK_COLOR)
        render.translateTo(fromX, fromY)
        render.renderLine(toX - fromX, toY - fromY)
    }

    private static String initials(String title) {
        title.split(/\s+/).findAll { it }.take(2).collect { it.substring(0, 1).toUpperCase() }.join('')
    }

    private void drawImage(OrgChartNode n, BigDecimal centerX, BigDecimal y) {
        render.fillStyle(IMAGE_PLACEHOLDER_COLOR)
        if (n.image) {
            render.translateTo(centerX - IMAGE_SIZE / 2, y)
            render.renderImage(n.image, IMAGE_SIZE, IMAGE_SIZE)
        } else {
            render.translateTo(centerX, y + IMAGE_SIZE / 2)
            render.renderCircle(IMAGE_SIZE / 2, IDiagramRender.DiagramStyle.fill)
            String text = initials(n.title)
            render.translateTo(centerX - render.measureText(text) / 2, y + (IMAGE_SIZE - fontSize) / 2)
            render.renderLabel(text)
        }
    }

    private void drawCenteredText(String text, BigDecimal centerX, BigDecimal y, BigDecimal maxWidth, boolean isSmall) {
        String fitted = fitText(text, maxWidth, isSmall)
        BigDecimal width = isSmall ? render.measureSmallText(fitted) : render.measureText(fitted)
        render.translateTo(centerX - width / 2, y)
        if (isSmall) render.renderSmallLabel(fitted)
        else render.renderLabel(fitted)
    }

    private void drawBox(OrgChartNode n, BigDecimal x, BigDecimal y, int depth) {
        String clickActionUrl = diagramOption?.clickActionUrl
        if (clickActionUrl) render.renderGroup(['element-type': ElementType.TOOLTIP, 'diagram-action-url': clickActionUrl, 'data-x': n.key])
        render.translateTo(x, y)
        render.fillStyle(BOX_COLOR)
        render.renderRect(boxWidth, boxHeight, IDiagramRender.DiagramStyle.fill)
        render.fillStyle(getKeyColor(depth))
        render.renderRect(boxWidth, BOX_ACCENT_THICKNESS, IDiagramRender.DiagramStyle.fill)
        render.renderRect(boxWidth, boxHeight, IDiagramRender.DiagramStyle.stroke)

        BigDecimal centerX = x + boxWidth / 2
        BigDecimal textWidth = boxWidth - BOX_PADDING * 2
        BigDecimal rowY = y + BOX_ACCENT_THICKNESS + BOX_PADDING
        if (hasImageRow) {
            drawImage(n, centerX, rowY)
            rowY += IMAGE_SIZE + LINE_GAP
        }
        if (hasHeaderRow) {
            if (n.header) drawCenteredText(n.header, centerX, rowY, textWidth, true)
            rowY += smallFontSize() + LINE_GAP
        }
        drawCenteredText(n.title, centerX, rowY, textWidth, false)
        if (n.subtitle) drawCenteredText(n.subtitle, centerX, rowY + fontSize + LINE_GAP, textWidth, true)
        if (clickActionUrl) render.renderGroupEnd()
    }

    /**
     * Draws leaves one below the other, linked to a vertical spine starting at spineTop.
     */
    private void drawStack(List<OrgChartNode> leaves, BigDecimal x, BigDecimal y, BigDecimal spineTop, int depth) {
        BigDecimal spineX = x + STACK_INDENT / 2
        BigDecimal lastMiddle = y + stackHeight(leaves.size()) - boxHeight / 2
        drawLine(spineX, spineTop, spineX, lastMiddle)
        leaves.eachWithIndex { OrgChartNode leaf, int i ->
            BigDecimal leafY = y + i * (boxHeight + STACK_GAP)
            drawLine(spineX, leafY + boxHeight / 2, x + STACK_INDENT, leafY + boxHeight / 2)
            drawBox(leaf, x + STACK_INDENT, leafY, depth)
        }
    }

    private void drawSubtree(OrgChartNode n, BigDecimal x, BigDecimal y, int depth) {
        if (isLeaf(n)) {
            drawBox(n, x, y, depth)
            return
        }
        if (stacksChildren(n)) {
            drawBox(n, x, y, depth)
            drawStack(children(n), x, y + boxHeight + STACK_GAP, y + boxHeight, depth + 1)
            return
        }

        BigDecimal width = subtreeWidth(n)
        BigDecimal parentCenterX = x + width / 2
        drawBox(n, parentCenterX - boxWidth / 2, y, depth)

        List<List<ChildColumn>> rows = childRows(n)
        boolean hasTrunk = rows.size() > 1
        BigDecimal blockWidth = childrenBlockWidth(rows)
        BigDecimal blockX = x + (width - blockWidth) / 2
        BigDecimal trunkX = blockX + TRUNK_MARGIN / 2
        BigDecimal rowY = y + boxHeight + VERTICAL_GAP
        BigDecimal firstBarY = rowY - VERTICAL_GAP / 2
        BigDecimal lastBarY = firstBarY
        drawLine(parentCenterX, y + boxHeight, parentCenterX, firstBarY)

        rows.eachWithIndex { List<ChildColumn> row, int i ->
            BigDecimal barY = rowY - VERTICAL_GAP / 2
            List<BigDecimal> connectorXs = []
            if (i == 0) connectorXs.add(parentCenterX)
            if (hasTrunk) connectorXs.add(trunkX)
            BigDecimal columnX = hasTrunk ? blockX + TRUNK_MARGIN : blockX
            for (ChildColumn column : row) {
                if (column.branch) {
                    BigDecimal childCenterX = columnX + boxCenterOffset(column.branch)
                    connectorXs.add(childCenterX)
                    drawLine(childCenterX, barY, childCenterX, rowY)
                    drawSubtree(column.branch, columnX, rowY, depth + 1)
                } else {
                    connectorXs.add(columnX + STACK_INDENT / 2)
                    drawStack(column.leaves, columnX, rowY, barY, depth + 1)
                }
                columnX += column.width + HORIZONTAL_GAP
            }
            drawLine(connectorXs.min(), barY, connectorXs.max(), barY)
            lastBarY = barY
            rowY += rowHeight(row) + VERTICAL_GAP
        }
        if (hasTrunk) drawLine(trunkX, firstBarY, trunkX, lastBarY)
    }

    @Override
    void draw(boolean alwaysShowFullInfo = false, Integer comboTotalCount = 0, Integer comboCurrentCount = 1) {
        super.draw(alwaysShowFullInfo, comboTotalCount, comboCurrentCount)
        drawTitle()

        BigDecimal x = (render.getDiagramWidth() - forestWidth()) / 2
        BigDecimal y = CHART_MARGIN + titleHeight()
        for (OrgChartNode root : branchRoots()) {
            drawSubtree(root, x, y, 0)
            x += subtreeWidth(root) + HORIZONTAL_GAP
        }
        for (List<OrgChartNode> stack : leafStacks(leafRoots())) {
            stack.eachWithIndex { OrgChartNode root, int i ->
                drawBox(root, x, y + i * (boxHeight + STACK_GAP), 0)
            }
            x += boxWidth + HORIZONTAL_GAP
        }
    }
}
