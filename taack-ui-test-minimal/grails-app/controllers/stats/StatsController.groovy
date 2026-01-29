package stats

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.runtime.MethodClosure
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.common.ActionIcon

@GrailsCompileStatic
@Secured(['permitAll'])
class StatsController {

    TaackUiService taackUiService
    StatsService statsService

    static UiMenuSpecifier buildMenu() {
        new UiMenuSpecifier().ui {
            menu StatsController.&topCustomerSales2 as MethodClosure
            menuIcon ActionIcon.ADD, StatsController.&topCustomerSales3 as MethodClosure
        }
    }

    def index() {
        redirect(action: 'topCustomerSales2')
    }

    def topCustomerSales2() {

        println "topCustomerSales2: ${params}"


        boolean p1 = params.int('v') == 1
        boolean p2 = params.int('v') == 2

        taackUiService.show(new UiBlockSpecifier().ui {
            table statsService.buildTable()

            row {
                poke p1, {
                    println "We pass on poke1"
                    col BlockSpec.Width.HALF, {
                        println "We pass on col1"
                        diagram(statsService.&buildChart1 as MethodClosure) {
                            println "We pass on diagram1"
                            label 'Sales1'
                            boolean showMonthlyGraph = params.boolean('showMonthlyGraph1')
                            boolean groupPerMonth = params.boolean('groupPerMonth1')

                            if (showMonthlyGraph) {
                                menu 'Yearly', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph1: 'false']
                                if (!groupPerMonth) menu 'Group by month', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph1: 'true'] + [groupPerMonth1: 'true']
                                else menu 'Ungroup', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph1: 'true'] + [groupPerMonth1: 'false']
                            } else {
                                menu 'Monthly', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph1: 'true']
                            }
                        }

                    }
                }
                poke p2, {
                    println "We pass on poke2"
                    col BlockSpec.Width.HALF, {
                        println "We pass on col2"
                        diagram(statsService::buildChart2 as MethodClosure) {
                            println "We pass on diagram2"
                            label 'Sales2'
                            boolean showMonthlyGraph = params.boolean('showMonthlyGraph2')
                            boolean groupPerMonth = params.boolean('groupPerMonth2')
                            if (showMonthlyGraph) {
                                menu 'Yearly', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph2: 'false']
                                if (!groupPerMonth) menu 'Group by month', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph2: 'true'] + [groupPerMonth2: 'true']
                                else menu 'Ungroup', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph2: 'true'] + [groupPerMonth2: 'false']
                            } else {
                                menu 'Monthly', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph2: 'true']
                            }
                        }
                    }
                }
            }
//        })
//        }, buildMenu())
        }, params.boolean('isAjax') ? null : buildMenu(), 'showMonthlyGraph1', 'groupPerMonth1', 'showMonthlyGraph2', 'groupPerMonth2')
//        })
    }
    def topCustomerSales3() {

        println "topCustomerSales3: ${params}"

        boolean p0 = params.int('v') == 0
        boolean p1 = params.int('v') == 1
        boolean p2 = params.int('v') == 2

        taackUiService.show(new UiBlockSpecifier().ui {
            modal {
                poke p0, { // Needed to refresh links with parameter to keep
                    table statsService.buildTable()
                }

                row {
                    poke p1, {
                        println "We pass on poke1"
                        col BlockSpec.Width.HALF, {
                            println "We pass on col1"
                            diagram(statsService.&buildChart1 as MethodClosure) {
                                println "We pass on diagram1"
                                label 'Sales1'
                                boolean showMonthlyGraph = params.boolean('showMonthlyGraph1')
                                boolean groupPerMonth = params.boolean('groupPerMonth1')
                                if (showMonthlyGraph) {
                                    menu 'Yearly', StatsController.&topCustomerSales3 as MethodClosure, [showMonthlyGraph1: 'false', v: 0]
                                    if (!groupPerMonth) menu 'Group by month', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph1: 'true', v: 0, groupPerMonth1: 'true']
                                    else menu 'Ungroup', StatsController.&topCustomerSales3 as MethodClosure, [showMonthlyGraph1: 'true', v: 0, groupPerMonth1: 'false']
                                } else {
                                    menu 'Monthly', StatsController.&topCustomerSales3 as MethodClosure, [showMonthlyGraph1: 'true', v: 0]
                                }
                            }

                        }
                    }
                    poke p2, {
                        println "We pass on poke2"
                        col BlockSpec.Width.HALF, {
                            println "We pass on col2"
                            diagram(statsService::buildChart2 as MethodClosure) {
                                println "We pass on diagram2"
                                label 'Sales2'
                                boolean showMonthlyGraph = params.boolean('showMonthlyGraph2')
                                boolean groupPerMonth = params.boolean('groupPerMonth2')
                                if (showMonthlyGraph) {
                                    menu 'Yearly', StatsController.&topCustomerSales3 as MethodClosure, [showMonthlyGraph2: 'false', v: 0]
                                    if (!groupPerMonth) menu 'Group by month', StatsController.&topCustomerSales2 as MethodClosure, [showMonthlyGraph2: 'true', v: 0, groupPerMonth2: 'true']
                                    else menu 'Ungroup', StatsController.&topCustomerSales3 as MethodClosure, [showMonthlyGraph2: 'true', v: 0, groupPerMonth2: 'false']
                                } else {
                                    menu 'Monthly', StatsController.&topCustomerSales3 as MethodClosure, [showMonthlyGraph2: 'true', v: 0]
                                }
                            }
                        }
                    }
                }
            }
//        })
//        }, buildMenu())
        }, 'showMonthlyGraph1', 'groupPerMonth1', 'showMonthlyGraph2', 'groupPerMonth2') // Not working, table is not refreshed
//        })
    }

}

