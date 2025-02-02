package taack.ui.dsl.block

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.dsl.*
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
class BlockLeafSpec {
    final IUiBlockVisitor blockVisitor
    final String filterTableId

    int counter = 0
    int ajaxCounter = 0

    BlockLeafSpec(final IUiBlockVisitor blockVisitor) {
        this.blockVisitor = blockVisitor
        this.filterTableId = blockVisitor.parameterMap?['filterTableId']
    }

    void processMenuBlock(String ajaxBlockId, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure) {
//        if (blockVisitor.doRenderElement(null)) {
        if (closure) {
            blockVisitor.visitBlockHeader()
            blockVisitor.visitMenuStart(MenuSpec.MenuMode.HORIZONTAL, ajaxBlockId)
            closure.delegate = new MenuSpec(blockVisitor) //menuSpec
            closure.call()
            counter++
            blockVisitor.visitMenuStartEnd()
            blockVisitor.visitBlockHeaderEnd()
        }
//        }
    }


    private final String theAjaxBlockId(String suffix) {
        if (blockVisitor.getExplicitAjaxBlockId())
            return blockVisitor.getExplicitAjaxBlockId()
        if (ajaxCounter > 64_000)
            ajaxCounter = 0
        ajaxCounter++
        return "ajaxBlockId${ajaxCounter}$suffix"
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
        String aId = theAjaxBlockId('form')
        if (blockVisitor.doRenderElement(aId)) {
            blockVisitor.visitAjaxBlock(aId)
            processMenuBlock(aId, closure)
            blockVisitor.visitForm(formSpecifier)
            blockVisitor.visitAjaxBlockEnd()
        }
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
        String aId = theAjaxBlockId('show')
        if (blockVisitor.doRenderElement(aId)) {
            blockVisitor.visitAjaxBlock(aId)
            processMenuBlock(aId, closure)
            blockVisitor.visitShow(showSpecifier)
            blockVisitor.visitAjaxBlockEnd()
        }
    }

    void table(final UiTableSpecifier tableSpecifier,
               @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId = theAjaxBlockId('table')
        if (blockVisitor.doRenderElement(aId)) {
            blockVisitor.visitAjaxBlock(aId)
            processMenuBlock(aId, closure)
            blockVisitor.visitTable(aId, tableSpecifier)
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
                     @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId = theAjaxBlockId('tableFilter')
        if (blockVisitor.doRenderElement(aId)) {
            blockVisitor.visitAjaxBlock(aId)
            processMenuBlock(aId, closure)
            blockVisitor.visitRow()
            blockVisitor.visitTableFilter(aId, filterSpecifier, tableSpecifier)
            blockVisitor.visitRowEnd()
            blockVisitor.visitAjaxBlockEnd()
        }
    }


    /**
     * Add a chart to the block
     *
     * @param diagramSpecifier description of the Chart. See {@link UiDiagramSpecifier}
     * @param closure menu
     */
    void diagram(final UiDiagramSpecifier diagramSpecifier,
                 @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId = theAjaxBlockId('chart')
        if (blockVisitor.doRenderElement(aId)) {
            blockVisitor.visitAjaxBlock(aId)
            processMenuBlock(aId, closure)
            blockVisitor.visitDiagram(diagramSpecifier)
            blockVisitor.visitAjaxBlockEnd()
        }

    }

    void diagramFilter(final UiFilterSpecifier filterSpecifier,
                       final UiDiagramSpecifier diagramSpecifier,
                       @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId = theAjaxBlockId('chartFilter')
        if (blockVisitor.doRenderElement(aId)) {
            blockVisitor.visitAjaxBlock(aId)
            processMenuBlock(aId, closure)
            blockVisitor.visitRow()
            blockVisitor.visitDiagramFilter(diagramSpecifier, filterSpecifier)
            blockVisitor.visitRowEnd()
            blockVisitor.visitAjaxBlockEnd()
        }
    }

    /**
     * Add custom HTML code in a block
     *
     * @param html code
     * @param style the template style to use
     * @param closure actions to display in the header
     */
    void custom(final String html, final Style style = null,
                @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure closure = null) {
        String aId = theAjaxBlockId('custom')
        if (blockVisitor.doRenderElement(aId)) {
            blockVisitor.visitAjaxBlock(aId)
            processMenuBlock(aId, closure)
            blockVisitor.visitCustom(html, style)
            blockVisitor.visitAjaxBlockEnd()
        }
    }
    /**
     * Add custom HTML code in a block
     *
     * @param html code
     * @param style the template style to use
     * @param closure actions to display in the header
     */
    void iframe(final String url, String cssHeight) {
        blockVisitor.visitIframe(url, cssHeight)
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


}
