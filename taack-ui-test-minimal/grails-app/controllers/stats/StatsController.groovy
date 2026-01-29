package stats

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.runtime.MethodClosure
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.dsl.block.BlockSpec

@GrailsCompileStatic
@Secured(['permitAll'])
class StatsController {

    TaackUiService taackUiService
    StatsService statsService

    static UiMenuSpecifier buildMenu() {
        new UiMenuSpecifier().ui {
            menu StatsController.&topCustomerSales2 as MethodClosure
        }
    }

    def index() {
        redirect(action: 'topCustomerSales2')
    }

    def topCustomerSales2() {

        println "topCustomerSales2: ${params}"

        boolean showMonthlyGraph = params.boolean('showMonthlyGraph')
        boolean groupPerMonth = params.boolean('groupPerMonth')

        boolean p1 = params.int('v') == 1 || params.get('v') == null
        boolean p2 = params.int('v') == 2 || params.get('v') == null

        taackUiService.show(new UiBlockSpecifier().ui {
            table statsService.buildTable()

            row {
                poke p1, {
                    println "We pass on poke1"
                    col BlockSpec.Width.HALF, {
                        println "We pass on col1"
                        diagram(statsService.&buildChart as MethodClosure) {
                            println "We pass on diagram1"
                            label 'Sales1'
                            if (showMonthlyGraph) {
                                menu 'Yearly', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'false']
                                if (!groupPerMonth) menu 'Group by month', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'true'] + [groupPerMonth: 'true']
                                else menu 'Ungroup', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'true'] + [groupPerMonth: 'false']
                            } else {
                                menu 'Monthly', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'true']
                            }
                        }

                    }
                }
                poke p2, {
                    println "We pass on poke2"
                    col BlockSpec.Width.HALF, {
                        println "We pass on col2"
                        diagram(statsService::buildChart as MethodClosure) {
                            println "We pass on diagram2"
                            label 'Sales2'
                            if (showMonthlyGraph) {
                                menu 'Yearly', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'false']
                                if (!groupPerMonth) menu 'Group by month', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'true'] + [groupPerMonth: 'true']
                                else menu 'Ungroup', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'true'] + [groupPerMonth: 'false']
                            } else {
                                menu 'Monthly', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph: 'true']
                            }
                        }
                    }
                }
            }
//        })
//        }, buildMenu())
        }, params.boolean('isAjax') ? null : buildMenu())
    }

}

