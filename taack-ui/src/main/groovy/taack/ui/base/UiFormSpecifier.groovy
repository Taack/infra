package taack.ui.base

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.base.form.FormSpec
import taack.ui.base.form.IUiFormVisitor

// TODO: Move TaackSimpleSave in taack-ui module
/**
 * Class allowing to manipulate the form to display in a block (see {@link taack.ui.base.block.BlockSpec#form(java.lang.String, taack.ui.base.UiFormSpecifier)}).
 */
@CompileStatic
final class UiFormSpecifier {
    Closure closure
    Object aObject
    FieldInfo[] lockedFields

    /**
     * Allow to draw the form
     *
     * @param aObject object the form is applied
     * @param lockedFields read only fields. This parameter should also be passed to the save method (declared in the crew app)
     * @param closure description of the form
     * @return itself
     */
    UiFormSpecifier ui(final Object aObject, final FieldInfo[] lockedFields = null, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FormSpec) final Closure closure) {
        this.closure = closure
        this.aObject = aObject
        this.lockedFields = lockedFields
        this
    }

    /**
     * Allow to visit the closure passed via {@link #ui(java.lang.Object, groovy.lang.Closure)}.
     *
     * @param formVisitor must implement {@link IUiFormVisitor}
     */
    void visitForm(final IUiFormVisitor formVisitor) {
        if (formVisitor && closure) {
            formVisitor.visitForm(aObject, lockedFields)
            closure.delegate = new FormSpec(formVisitor)
            closure.call()
            formVisitor.visitFormEnd()
        }
    }
}