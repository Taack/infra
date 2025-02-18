package taack.ui

import grails.compiler.GrailsCompileStatic
import taack.ast.type.FieldInfo
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiFormSpecifier
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.form.FormSpec
import taack.ui.dsl.menu.MenuSpec
import taack.ui.dsl.table.TableSpec

@GrailsCompileStatic
final class TaackUi {

    static UiMenuSpecifier createMenu(
            @DelegatesTo(strategy = Closure. DELEGATE_ONLY, value = MenuSpec) Closure closure
    ) {
        new UiMenuSpecifier().ui(closure)
    }

    static UiMenuSpecifier mergeMenu(UiMenuSpecifier original, UiMenuSpecifier addOns) {
        if (addOns && addOns.closure)
            original.closure = original.closure << addOns.closure
        original
    }

    static UiBlockSpecifier createModal(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        new UiBlockSpecifier().ui {
            modal(closure)
        }
    }

    static UiFormSpecifier createForm(final Object aObject, final FieldInfo[] lockedFields = null, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FormSpec) final Closure closure) {
        new UiFormSpecifier().ui(aObject, lockedFields, closure)
    }

    static UiTableSpecifier createTable(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = TableSpec) final Closure closure) {
        new UiTableSpecifier().ui(closure)
    }
}
