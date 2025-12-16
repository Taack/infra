package taack.ui

import grails.compiler.GrailsCompileStatic
import taack.ast.type.FieldInfo
import taack.domain.TaackFilter
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiFilterSpecifier
import taack.ui.dsl.UiFormSpecifier
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.filter.FilterSpec
import taack.ui.dsl.form.FormSpec
import taack.ui.dsl.menu.MenuSpec
import taack.ui.dsl.table.TableSpec

@GrailsCompileStatic
final class TaackUi {

    static UiMenuSpecifier createMenu(
            @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = MenuSpec) Closure closure
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

    static UiBlockSpecifier createModalForm(final Object aObject, final FieldInfo[] lockedFields = null, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FormSpec) final Closure closure) {
        new UiBlockSpecifier().ui {
            modal {
                form createForm(aObject, lockedFields, closure)
            }
        }
    }

    static UiFormSpecifier createForm(final Object aObject, final FieldInfo[] lockedFields = null, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FormSpec) final Closure closure) {
        new UiFormSpecifier().ui(aObject, lockedFields, closure)
    }

    static UiTableSpecifier createTable(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = TableSpec) final Closure closure) {
        new UiTableSpecifier().ui(closure)
    }

    /**
     * Create filter table block
     * @param aClass
     * @param cFilter
     * @param tFilter
     * @return
     */
    static UiBlockSpecifier createBlock(final Class aClass,
                                        @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FilterSpec) final Closure cFilter,
                                        @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = TableSpec) final Closure cTable,
                                        @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure cMenu = null) {
        new UiBlockSpecifier().ui {
            tableFilter(new UiFilterSpecifier().ui(aClass, cFilter), new UiTableSpecifier().ui(cTable), cMenu)
        }
    }

    static UiBlockSpecifier createBlock(boolean isModal, final Class aClass,
                                        @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FilterSpec) final Closure cFilter,
                                        @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = TableSpec) final Closure cTable,
                                        @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MenuSpec) final Closure cMenu = null) {
        if (isModal) {
            new UiBlockSpecifier().ui {
                modal {
                    tableFilter(new UiFilterSpecifier().ui(aClass, cFilter), new UiTableSpecifier().ui(cTable), cMenu)
                }
            }
        } else createBlock(aClass, cFilter, cTable, cMenu)
    }
}
