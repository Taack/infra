package taack.ui.dsl

import groovy.transform.CompileStatic
import taack.ui.dsl.menu.IUiMenuVisitor
import taack.ui.dsl.menu.MenuSpec

/**
 * Allow to specify top menu. Those menus are limited to 2 levels and
 * labels are automatically computed.
 *
 *  <pre>
 *  new UiMenuSpecifier().ui {
 *      menu CrewController.&index as MC
 *      menu CrewController.&listRoles as MC
 *      menuIcon ActionIcon.CONFIG_USER, this.&editUser as MC
 *      menuIcon ActionIcon.EXPORT_CSV, this.&downloadBinPdf as MC
 *      label "Admin", {
 *          subMenu this.&confSites as MC
 *          subMenu this.&menuEntries as MC
 *          subMenu this.&testDiagram as MC
 *      }
 *      menuSearch this.&search as MethodClosure, q
 *      menuOptions(SupportedLanguage.fromContext())
 *  }
 *  </pre>
 */
@CompileStatic
final class UiMenuSpecifier {
    Closure closure

    UiMenuSpecifier ui(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = MenuSpec) Closure closure) {
        this.closure = closure
        this
    }

    void visitMenu(final IUiMenuVisitor menuVisitor) {
        if (menuVisitor && closure) {
            menuVisitor.visitMenuStart(MenuSpec.MenuMode.HORIZONTAL, null)
            closure.delegate = new MenuSpec(menuVisitor)
            closure.call()
            menuVisitor.visitMenuStartEnd()
        }
    }
}
