package stats

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.runtime.MethodClosure
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.dsl.block.BlockSpec

@GrailsCompileStatic
@Secured(['ROLE_ADMIN'])
class StatsController {

    TaackUiService taackUiService
    StatsService statsService

    static UiMenuSpecifier buildMenu() {
        new UiMenuSpecifier().ui {
            menu StatsController.&topCustomerSales2 as MethodClosure
        }
    }

    def index() {
        redirect(action: 'topCustomerSales')
    }

    def topCustomerSales2() {
        boolean showMonthlyGraph = params.boolean('showMonthlyGraph')
        boolean groupPerMonth = params.boolean('groupPerMonth')

        taackUiService.show(new UiBlockSpecifier().ui {
            row {
                col BlockSpec.Width.HALF, {
                    println 'coucou'
                    diagramMc(statsService.&buildChart as MethodClosure) {
                        label "Sales1"
                        if (showMonthlyGraph) {
                            menu "Yearly", StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'false']
                            if (!groupPerMonth) menu "Group by month", StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'true'] + [groupPerMonth: 'true']
                            else menu "Ungroup", StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'true'] + [groupPerMonth: 'false']
                        } else {
                            menu "Monthly", StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'true']
                        }
                    }

                }
                col BlockSpec.Width.HALF, {
                    println 'coucou2'
                    diagramMc(statsService::buildChart as MethodClosure) {
                        label "Sales2"
                        if (showMonthlyGraph) {
                            menu "Yearly", StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'false']
                            if (!groupPerMonth) menu "Group by month", StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'true'] + [groupPerMonth: 'true']
                            else menu "Ungroup", StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'true'] + [groupPerMonth: 'false']
                        } else {
                            menu "Monthly", StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'true']
                        }
                    }
                }
            }
//        }, (params.boolean('isAjax') ? null :  buildMenu()))
        })
    }

}

