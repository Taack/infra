package taack.ui.base.block

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.base.*
import taack.ui.base.common.Style
import taack.ui.base.menu.MenuSpec

// TODO: try to remove ajaxBlock
// TODO: try to remove modal first param
// TODO: to improve completion, isolate ajaxTab from BlockSpec
// TODO: Turns ajaxBlock first parameter to be optional
/**
 * {@link UiBlockSpecifier#ui(groovy.lang.Closure)} delegated class.
 *
 * <p>This class allows to draw the layout of a page, or to update part of a page after an ajax call.
 *
 * <p>Each block can contains many graphical elements, but it is better to have one graphical
 * element (show, form, table, tableFilter ...) per block (modal, ajaxBlock ...)
 */
@CompileStatic
final class BlockSpec {
    final IUiBlockVisitor blockVisitor
    final MenuSpec menuSpec
    final String filterTableId

    int counter = 0
    static int ajaxCounter = 0
    String id
    String currentBlockId

    BlockSpec(final IUiBlockVisitor blockVisitor) {
        this.blockVisitor = blockVisitor
        this.menuSpec = new MenuSpec(blockVisitor)
        this.filterTableId = blockVisitor.parameterMap['filterTableId']
    }

    private final boolean displayElement(final String id = null) {
        if (!filterTableId) return true
        if (id) currentBlockId = id
        else currentBlockId = filterTableId
    }

    private final String getAjaxBlockId() {
        if (ajaxCounter > 2>>30)
            ajaxCounter = 0
        blockVisitor.parameterMap['ajaxBlockId'] ?: "ajaxBlockId${ajaxCounter++}"
    }

    /**
     * Helper method allowing to split block creation. See {@link #inline(groovy.lang.Closure)} to
     * inject this block into another block.
     *
     * @param closure closure that should benefit from IDE completion and static type checking
     * @return typed closure
     */
    static Closure<BlockSpec> buildBlockSpec(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        closure
    }

    /**
     * Allow to inject a block part that has been created elsewhere.
     * See {@link #buildBlockSpec(groovy.lang.Closure)}
     *
     * @param blockSpecClosure
     */
    void inline(final Closure<BlockSpec> blockSpecClosure) {
        blockSpecClosure.delegate = this
        blockSpecClosure.call()
    }

    enum Width {
        MAX("pure-u-1", "pure-u-1", 'col-12'),
        THREE_QUARTER("pure-u-1 pure-u-md-3-4", "pure-u-3-4", 'col-12 col-md-9'),
        TWO_THIRD("pure-u-1 pure-u-md-2-3", "pure-u-2-3", 'col-12 col-md-8'),
        HALF("pure-u-1 pure-u-md-1-2", "pure-u-1-2", 'col-12 col-md-6'),
        THIRD("pure-u-1 pure-u-md-1-3", "pure-u-1-3", 'col-12 col-md-4'),
        QUARTER("pure-u-1 pure-u-md-1-4", "pure-u-1-4", 'col-12 col-md-3')

        Width(final String css, final String pdfCss, final String bootstrapCss) {
            this.css = css
            this.pdfCss = pdfCss
            this.bootstrapCss = bootstrapCss
        }

        final String css
        final String pdfCss
        final String bootstrapCss
    }

    /**
     * Embeds tab. The closure should contains only {@link #blockTab(java.lang.String, groovy.lang.Closure)} children.
     *
     * @param width width of the tabulation block
     * @param closure description of the tabulations
     */
    void blockTabs(final Width width = Width.MAX, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        if (displayElement()) blockVisitor.visitBlockTabs(width)
        closure.delegate = this
        closure.call()
        counter ++
        if (displayElement()) blockVisitor.visitBlockTabsEnd()
    }

    /**
     * Allow to describe the content of a tab. must be children of {@link #blockTabs(taack.ui.base.block.BlockSpec.Width, groovy.lang.Closure)}
     *
     * @param i18n label
     * @param closure content of the tabulation.
     */
    void blockTab(final String i18n, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        if (displayElement()) blockVisitor.visitBlockTab(i18n)
        closure.delegate = this
        closure.call()
        counter ++
        if (displayElement()) blockVisitor.visitBlockTabEnd()
    }

    /**
     * invisible blocks that enable complex layout. Can be nested.
     *
     * @param width
     * @param closure
     */
    void innerBlock(final Width width, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        if (displayElement()) blockVisitor.visitInnerColBlock(width)
        closure.delegate = this
        closure.call()
        counter ++
        if (displayElement()) blockVisitor.visitInnerColBlockEnd()
    }

    /**
     * invisible blocks that enable complex layout. Can be nested.
     *
     * @param width
     * @param closure
     */
    void innerRowBlock(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        if (displayElement()) blockVisitor.visitInnerRowBlock()
        closure.delegate = this
        closure.call()
        counter ++
        if (displayElement()) blockVisitor.visitInnerRowBlockEnd()
    }

    /**
     * Ajax block must be children of ajaxBlock.
     *
     * Mandatory invisible block that enable ajax.
     *
     * @param id uniq id in the page.
     * @param visitAjax set to false to process block like non ajax block
     * @param closure description of the user interface
     */
    void ajaxBlock(final String id, Boolean visitAjax = true, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        this.id = id
        if (displayElement(id)) {
            if (visitAjax) blockVisitor.visitAjaxBlock(id)
            closure.delegate = this
            closure.call()
            counter ++
            if (visitAjax) blockVisitor.visitAjaxBlockEnd()
        }
        displayElement("_-DoNotDisplayAnything-_")
    }

    /**
     * Pop a modal. Nested blocks will be displayed inside this modal.
     *
     * @param firstPass (optional) if true create a new modal, if false, replace the content of the top modal.
     * @param closure content of the modal
     */
    void modal(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        blockVisitor.visitModal()
        closure.delegate = this
        closure.call()
        counter ++
        blockVisitor.visitModalEnd()
    }


    private void processMenuBlock(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure) {
        if (closure) {
            blockVisitor.visitBlockHeader()
            blockVisitor.visitMenuStart(null)
            closure.delegate = new MenuSpec(blockVisitor) //menuSpec
            closure.call()
            counter ++
            blockVisitor.visitMenuEnd()
            blockVisitor.visitBlockHeaderEnd()
        }
    }

    /**
     * Add a form in a block
     *
     * @param i18n label in the header of the form
     * @param formSpecifier the form description see {@link UiFormSpecifier}
     * @param width
     * @param closure list of action in the header to add. See {@link MenuSpec}
     */
    void form(final UiFormSpecifier formSpecifier, final Width width = Width.MAX,
              @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId  = ajaxBlockId
        if (displayElement(aId)) {
            id = aId
            blockVisitor.visitAjaxBlock(id)
            blockVisitor.visitForm(width)
            processMenuBlock(closure)
            blockVisitor.visitFormEnd(formSpecifier)
            blockVisitor.visitAjaxBlockEnd()
        }
    }

    /**
     * Add a show in a block. See {@link UiShowSpecifier}. A show serves to display object data with limited edition
     * capabilities
     *
     * @param i18n label in the header of the show
     * @param showSpecifier the object data to display
     * @param width
     * @param closure list of action in the header to add. See {@link MenuSpec}
     */
    void show(final UiShowSpecifier showSpecifier, final Width width,
              @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId  = ajaxBlockId
        if (displayElement(aId)) {
            id = aId
            blockVisitor.visitAjaxBlock(id)
            blockVisitor.visitShow(width)
            processMenuBlock(closure)
            blockVisitor.visitShowEnd(showSpecifier)
            blockVisitor.visitAjaxBlockEnd()
        }
    }

    void table(final UiTableSpecifier tableSpecifier, final Width width = Width.MAX,
               @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId  = ajaxBlockId
        if (displayElement(aId)) {
            id = aId
            blockVisitor.visitAjaxBlock(id)
            blockVisitor.visitTable(id, width)
            processMenuBlock(closure)
            blockVisitor.visitTableEnd(tableSpecifier)
            blockVisitor.visitAjaxBlockEnd()
        }
    }

    /**
     * Add a filter and its associated table to the block
     *
     * @param i18nFilter filter label
     * @param filterSpecifier description of the filter
     * @param i18nTable table label
     * @param tableSpecifier description of the table
     * @param closure action ot display along with the label in the header
     */
    void tableFilter(final UiFilterSpecifier filterSpecifier,
                     final UiTableSpecifier tableSpecifier,
                     final Width width,
                     @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId  = ajaxBlockId
        if (displayElement(aId)) {
            id = aId
            blockVisitor.visitAjaxBlock(id)
            blockVisitor.visitTableFilter(id, filterSpecifier, width)
            processMenuBlock(closure)
            blockVisitor.visitTableFilterEnd(tableSpecifier)
            blockVisitor.visitAjaxBlockEnd()
        }
    }


    /**
     * Add a chart to the block
     *
     * @param i18n label of the chart
     * @param chartSpecifier description of the Chart. See {@link UiChartSpecifier}
     * @param width the with of the chart in the block
     * @param closure actions to add in the header of the chart
     */
    void chart(final UiChartSpecifier chartSpecifier, final Width width = Width.MAX,
               @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId  = ajaxBlockId
        if (displayElement(aId)) {
            id = aId
            blockVisitor.visitAjaxBlock(id)
            blockVisitor.visitChart(width)
            processMenuBlock(closure)
            blockVisitor.visitChartEnd(chartSpecifier)
            blockVisitor.visitAjaxBlockEnd()
        }
    }

    void diagram(final UiDiagramSpecifier diagramSpecifier, final Width width = Width.MAX) {
        if (displayElement()) {
            blockVisitor.visitDiagram(width)
            blockVisitor.visitDiagramEnd(diagramSpecifier, width)
        }
    }

    void diagramFilter(final UiFilterSpecifier filterSpecifier,
                       final UiDiagramSpecifier diagramSpecifier, final Width width = Width.MAX) {
        if (displayElement()) {
            blockVisitor.visitDiagramFilter(filterSpecifier, width)
            blockVisitor.visitDiagramEnd(diagramSpecifier, Width.THREE_QUARTER)
        }
    }

    /**
     * Add custom HTML code in a block
     *
     * @param i18n label of the block part
     * @param html code
     * @param style the template style to use
     * @param width width inside the block
     * @param closure actions to display in the header
     */
    void custom(final String html, final Style style = null, final Width width = Width.MAX,
                @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId  = ajaxBlockId
        if (displayElement(aId)) {
            id = aId
            blockVisitor.visitAjaxBlock(id)
            blockVisitor.visitCustom(html, style, width)
            counter ++
            blockVisitor.visitAjaxBlockEnd()
        }
    }

    /**
     * Close the topmost modal. Usually, it passes an ID and a label to a form in a many to many relationship, if you open a modal using
     * {@link taack.ui.base.form.FormSpec#ajaxField(taack.ast.type.FieldInfo, org.codehaus.groovy.runtime.MethodClosure)}.
     *
     * @param id the ID of the object
     * @param value the label of the object (should use toString() obejct method)
     * @param fields allow to update the form from which ajaxField has been called with the values of the fields listed
     */
    void closeModal(final String id, final String value, final FieldInfo[] fields = null) {
        if (displayElement()) blockVisitor.visitCloseModal(id, value, fields)
    }

    /**
     * Close the topmost modal. Usually, it passes an ID and a label to a form in a many to many relationship, if you open a modal using
     * {@link taack.ui.base.form.FormSpec#ajaxField(taack.ast.type.FieldInfo, org.codehaus.groovy.runtime.MethodClosure)}.
     *
     * @param id the ID of the object
     * @param value the label of the object (should use toString() obejct method)
     * @param fields allow to update the form from which ajaxField has been called with the values of the fields listed
     */
    void closeModal(final Long id, final String value, final FieldInfo[] fields = null) {
        if (displayElement()) blockVisitor.visitCloseModal(id?.toString(), value, fields)
    }

    /**
     * Close the topmost modal. Usually, it passes an ID and a label to a form in a many to many relationship, if you open a modal using
     * {@link taack.ui.base.form.FormSpec#ajaxField(taack.ast.type.FieldInfo, org.codehaus.groovy.runtime.MethodClosure)}.
     *
     * @param fields allow to update the form from which ajaxField has been called with the values of the fields listed
     */
    void closeModal(final FieldInfo[] fields) {
        if (displayElement()) blockVisitor.visitCloseModal(null, null, fields)
    }

    /**
     * Generic close. Close the topmost modal, and update the block of the pages.
     *
     * @param closure describe how to update the page. This closure must not target elements that are in the current modal.
     */
    void closeModalAndUpdateBlock(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        if (displayElement()) {
            blockVisitor.visitCloseModalAndUpdateBlock()
            closure.delegate = this
            closure.call()
            counter ++
            blockVisitor.visitCloseModalAndUpdateBlockEnd()
        }
    }
}
