package taack.ui.dsl

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.dsl.helper.Utils
import taack.ui.dsl.show.IUiShowVisitor
import taack.ui.dsl.show.ShowSpec

/**
 * Class allowing to build a show graphical element in a block.
 *
 * <p>To add a show graphical element in a block, call
 * {@link taack.ui.dsl.block.BlockSpec#show(UiShowSpecifier)}
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
final class UiShowSpecifier<T> {
    Closure closure

    /**
     * Describes the block to show
     *
     * @param aObject (optional) object to show
     * @param controller (optional) place holder for future inline edition
     * @param action (optional) place holder for future inline edition
     * @param closure description of what to show
     * @return itself
     */
    UiShowSpecifier ui(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ShowSpec) Closure closure) {
        this.closure = closure
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
    @Deprecated
    UiShowSpecifier ui(final T aObject, final MethodClosure action = null, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ShowSpec) Closure closure) {
        this.closure = closure
        this
    }

    /**
     * Visit the show description with a {@link taack.ui.dsl.show.IUiShowVisitor}
     *
     * @param showVisitor the visitor
     */
    void visitShow(final IUiShowVisitor showVisitor) {
        if (showVisitor && closure) {
            showVisitor.visitShow()
            closure.delegate = new ShowSpec(showVisitor)
            closure.call()
            showVisitor.visitShowEnd()
        }
    }
}