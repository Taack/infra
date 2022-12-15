package taack.ui.base

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.base.helper.Utils
import taack.ui.base.show.IUiShowVisitor
import taack.ui.base.show.ShowSpec

/**
 * Class allowing to build a show graphical element in a block.
 *
 * <p>To add a show graphical element in a block, call
 * {@link taack.ui.base.block.BlockSpec#show(java.lang.String, taack.ui.base.UiShowSpecifier, taack.ui.base.block.BlockSpec.Width)}
 *
 * <pre>{@code
 *  UiShowSpecifier buildShowItem(Item item, Subsidiary subsidiary) {
 *      new UiShowSpecifier().ui item, {
 *          inlineHtml(attachmentUiService.preview(item.attachments.find { it.type == AttachmentType.mainPicture && it.active }?.id, TaackSimpleAttachmentService.PreviewFormat.PREVIEW_MEDIUM), null)
 *          field item.name, Style.BLUE + Style.EMPHASIS
 *          field item.ref, Style.EMPHASIS
 *          fieldLabeled item.itemStatus.style, item.itemStatus_
 *          fieldLabeled item.labeling_
 *          fieldLabeled item.gtin_
 *          if (item.range)
 *              fieldAction "Show Range", ActionIcon.SHOW * ActionIconStyleModifier.SCALE_DOWN, Bp2Controller.&showRange as MethodClosure, [id: item.range.id, subsidiary: subsidiary.toString()], false
 *          fieldLabeled Style.BLUE, item.range_
 *          fieldLabeled item.dateCreated_
 *          fieldLabeled item.userCreated_
 *          fieldLabeled item.lastUpdated_
 *          fieldLabeled item.itemType_
 *      }
 *  }
 * }</pre>
 */
@CompileStatic
final class UiShowSpecifier {
    Closure closure
    Object object
    String controller
    String action

    /**
     * Describes the block to show
     *
     * @param aObject (optional) object to show
     * @param controller (optional) place holder for future inline edition
     * @param action (optional) place holder for future inline edition
     * @param closure description of what to show
     * @return itself
     */
    UiShowSpecifier ui(final Object aObject = null, final String controller = null, final String action = null, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ShowSpec) Closure closure) {
        this.closure = closure
        this.object = aObject
        this.controller = controller
        this.action = action
        this
    }

    /**
     * Describes the block to show
     *
     * @param aObject (optional) object to show
     * @param action place holder for future inline edition
     * @param closure description of what to show
     * @return itself
     */
    UiShowSpecifier ui(final Object aObject = null, final MethodClosure action, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ShowSpec) Closure closure) {
        this.closure = closure
        this.object = aObject
        this.controller = Utils.getControllerName(action)
        this.action = action.method
        this
    }

    /**
     * Visit the show description with a {@link IUiShowVisitor}
     *
     * @param showVisitor the visitor
     */
    void visitShow(final IUiShowVisitor showVisitor) {
        if (showVisitor && closure) {
            showVisitor.visitShow(object, controller, action)
            closure.delegate = new ShowSpec(showVisitor)
            closure.call()
            showVisitor.visitShowEnd()
        }
    }
}