package taack.ui.dsl.block

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.dsl.UiChartSpecifier
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.UiFilterSpecifier
import taack.ui.dsl.UiFormSpecifier
import taack.ui.dsl.UiShowSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.common.Style
import taack.ui.dsl.menu.MenuSpec

// TODO: try to remove ajaxBlock
// TODO: try to remove modal first param
// TODO: to improve completion, isolate ajaxTab from BlockSpec
// TODO: Turns ajaxBlock first parameter to be optional
/**
 * {@link taack.ui.dsl.UiBlockSpecifier#ui(groovy.lang.Closure)} delegated class.
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

    BlockSpec(final IUiBlockVisitor blockVisitor) {
        this.blockVisitor = blockVisitor
        this.menuSpec = new MenuSpec(blockVisitor)
        this.filterTableId = blockVisitor.parameterMap['filterTableId']
    }

    private final String getAjaxBlockId() {
        if (ajaxCounter > 2 >> 30)
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
        THREE_QUARTER("pure-u-1 pure-u-md-3-4", "pure-u-3-4", 'col-9'),
        TWO_THIRD("pure-u-1 pure-u-md-2-3", "pure-u-2-3", 'col-8'),
        HALF("pure-u-1 pure-u-md-1-2", "pure-u-1-2", 'col-6'),
        THIRD("pure-u-1 pure-u-md-1-3", "pure-u-1-3", 'col-4'),
        QUARTER("pure-u-1 pure-u-md-1-4", "pure-u-1-4", 'col-3')

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
     * Embeds tab. The closure should contains only {@link #tab(java.lang.String, groovy.lang.Closure)} children.
     *
     * @param width width of the tabulation block
     * @param closure description of the tabulations
     */
    void tabs(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        blockVisitor.visitBlockTabs()
        closure.delegate = this
        closure.call()
        counter++
        blockVisitor.visitBlockTabsEnd()
    }

    /**
     *
     * @param i18n label
     * @param closure content of the tabulation.
     */
    void tab(final String i18n, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        blockVisitor.visitBlockTab(i18n)
        closure.delegate = this
        closure.call()
        counter++
        blockVisitor.visitBlockTabEnd()
    }

    /**
     * invisible blocks that enable complex layout. Can be nested.
     *
     * @param width
     * @param closure
     */
    void col(final Width width = Width.HALF, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        blockVisitor.visitCol(width)
        closure.delegate = this
        closure.call()
        counter++
        blockVisitor.visitColEnd()
    }

    /**
     * invisible blocks that enable complex layout. Can be nested.
     *
     * @param width
     * @param closure
     */
    void row(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        blockVisitor.visitRow()
        closure.delegate = this
        closure.call()
        counter++
        blockVisitor.visitRowEnd()
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

        if (visitAjax) blockVisitor.visitAjaxBlock(id)
        closure.delegate = this
        closure.call()
        counter++
        if (visitAjax) blockVisitor.visitAjaxBlockEnd()

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
        counter++
        blockVisitor.visitModalEnd()
    }


    private void processMenuBlock(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure) {
        if (closure) {
            blockVisitor.visitBlockHeader()
            blockVisitor.visitMenuStart(null)
            closure.delegate = new MenuSpec(blockVisitor) //menuSpec
            closure.call()
            counter++
            blockVisitor.visitMenuStartEnd()
            blockVisitor.visitBlockHeaderEnd()
        }
    }

    /**
     * Add a form in a block
     *
     * @param i18n label in the header of the form
     * @param formSpecifier the form description see {@link taack.ui.dsl.UiFormSpecifier}
     * @param width
     * @param closure list of action in the header to add. See {@link MenuSpec}
     */
    void form(final UiFormSpecifier formSpecifier,
              @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId = ajaxBlockId
        id = aId
        blockVisitor.visitAjaxBlock(id)
        processMenuBlock(closure)
        blockVisitor.visitForm(formSpecifier)
        blockVisitor.visitAjaxBlockEnd()
    }

    /**
     * Add a show in a block. See {@link taack.ui.dsl.UiShowSpecifier}. A show serves to display object data with limited edition
     * capabilities
     *
     * @param i18n label in the header of the show
     * @param showSpecifier the object data to display
     * @param width
     * @param closure list of action in the header to add. See {@link MenuSpec}
     */
    void show(final UiShowSpecifier showSpecifier,
              @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId = ajaxBlockId
        id = aId
        blockVisitor.visitAjaxBlock(id)
        processMenuBlock(closure)
        blockVisitor.visitShow(showSpecifier)
        blockVisitor.visitAjaxBlockEnd()
    }

    void table(final UiTableSpecifier tableSpecifier,
               @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId = ajaxBlockId
        id = aId
        blockVisitor.visitAjaxBlock(id)
        processMenuBlock(closure)
        blockVisitor.visitTable(id, tableSpecifier)
        blockVisitor.visitAjaxBlockEnd()
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
                     @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId = ajaxBlockId
        id = aId
        blockVisitor.visitAjaxBlock(id)
        processMenuBlock(closure)
        blockVisitor.visitRow()
        blockVisitor.visitTableFilter(id, filterSpecifier, tableSpecifier)
        blockVisitor.visitRowEnd()
        blockVisitor.visitAjaxBlockEnd()
    }


    /**
     * Add a chart to the block
     *
     * @param i18n label of the chart
     * @param chartSpecifier description of the Chart. See {@link taack.ui.dsl.UiChartSpecifier}
     * @param width the with of the chart in the block
     * @param closure actions to add in the header of the chart
     */
    void chart(final UiChartSpecifier chartSpecifier,
               @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId = ajaxBlockId
        id = aId
        blockVisitor.visitAjaxBlock(id)
        processMenuBlock(closure)
        blockVisitor.visitChart(chartSpecifier)
        blockVisitor.visitAjaxBlockEnd()
    }

    void diagram(final UiDiagramSpecifier diagramSpecifier) {

        blockVisitor.visitDiagram(diagramSpecifier)

    }

    void diagramFilter(final UiFilterSpecifier filterSpecifier,
                       final UiDiagramSpecifier diagramSpecifier) {

        blockVisitor.visitDiagramFilter(diagramSpecifier, filterSpecifier)

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
    void custom(final String html, final Style style = null,
                @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId = ajaxBlockId
        id = aId
        blockVisitor.visitAjaxBlock(id)
        blockVisitor.visitCustom(html, style)
        counter++
        blockVisitor.visitAjaxBlockEnd()
    }

    /**
     * Close the topmost modal. Usually, it passes an ID and a label to a form in a many to many relationship, if you open a modal using
     * {@link taack.ui.dsl.form.FormSpec#ajaxField(taack.ast.type.FieldInfo, org.codehaus.groovy.runtime.MethodClosure)}.
     *
     * @param id the ID of the object
     * @param value the label of the object (should use toString() obejct method)
     * @param fields allow to update the form from which ajaxField has been called with the values of the fields listed
     */
    void closeModal(final String id, final String value, final FieldInfo[] fields = null) {
        blockVisitor.visitCloseModal(id, value, fields)
    }

    /**
     * Close the topmost modal. Usually, it passes an ID and a label to a form in a many to many relationship, if you open a modal using
     * {@link taack.ui.dsl.form.FormSpec#ajaxField(taack.ast.type.FieldInfo, org.codehaus.groovy.runtime.MethodClosure)}.
     *
     * @param id the ID of the object
     * @param value the label of the object (should use toString() obejct method)
     * @param fields allow to update the form from which ajaxField has been called with the values of the fields listed
     */
    void closeModal(final Long id, final String value, final FieldInfo[] fields = null) {
        blockVisitor.visitCloseModal(id?.toString(), value, fields)
    }

    /**
     * Close the topmost modal. Usually, it passes an ID and a label to a form in a many to many relationship, if you open a modal using
     * {@link taack.ui.dsl.form.FormSpec#ajaxField(taack.ast.type.FieldInfo, org.codehaus.groovy.runtime.MethodClosure)}.
     *
     * @param fields allow to update the form from which ajaxField has been called with the values of the fields listed
     */
    void closeModal(final FieldInfo[] fields) {
        blockVisitor.visitCloseModal(null, null, fields)
    }

    /**
     * Generic close. Close the topmost modal, and update the block of the pages.
     *
     * @param closure describe how to update the page. This closure must not target elements that are in the current modal.
     */
    void closeModalAndUpdateBlock(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        blockVisitor.visitCloseModalAndUpdateBlock()
        closure.delegate = this
        closure.call()
        counter++
        blockVisitor.visitCloseModalAndUpdateBlockEnd()
    }
}
