package taack.ui.dsl.form


import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.helper.Utils
import taack.ui.dump.html.element.ButtonStyle

// TODO: Isolate sectionTab
/**
 * {@link taack.ui.dsl.UiFormSpecifier#ui(java.lang.Object, groovy.lang.Closure)} delegated class.
 *
 * <p>This class allow to draw a form. A form is composed of sections, field and tabs.
 *
 * <p>A form is included into a block calling {@link taack.ui.dsl.block.BlockSpec#form(java.lang.String, taack.ui.dsl.UiFormSpecifier)}.
 *
 * <p>Should help with autocompletion and static typing validation of the fields of the form that are displayed in the
 * browser.
 */
@CompileStatic
final class FormSpec extends FormRowSpec {

    /**
     * Form sections relative Width
     */

    FormSpec(IUiFormVisitor formVisitor) {
        super(formVisitor)
    }

    /**
     * When one of the field is updated, the form is refreshed
     *
     * @param fields
     */
    void triggerUpdate(FieldInfo... fields) {
        formVisitor.visitTriggerUpdate(fields)
    }

    /**
     * form action. The form is POSTed to the target action.
     *
     * @param i18n label of the button
     * @param action methodClosure pointing to the action
     * @param id id param
     * @param params additional params
     */
    void formAction(String i18n, final MethodClosure action, final Long id = null, final Map params = null, ButtonStyle style = ButtonStyle.SUCCESS) {
        if (taackUiEnablerService.hasAccess(action, id, params)) formVisitor.visitFormAction(i18n, Utils.getControllerName(action), action.method, id, params, style)
    }

    void formAction(final MethodClosure action, final Long id = null, final Map params = null, ButtonStyle style = ButtonStyle.SUCCESS) {
        formAction(null, action, id, params, style)
    }

    void formAction(String i18n, String url, ButtonStyle style = ButtonStyle.SUCCESS) {
        formVisitor.visitFormAction(i18n, url, style)
    }
}
