package stats


import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import grails.web.api.WebAttributes
import lodomain.TestInlineEdit
import org.codehaus.groovy.runtime.MethodClosure as MC
import taack.render.TaackSaveService
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.common.ActionIcon

import static taack.ui.TaackUi.*

@GrailsCompileStatic
@Secured(['permitAll'])
class StatsController implements WebAttributes {

    TaackUiService taackUiService
    StatsService statsService

    static UiMenuSpecifier buildMenu() {
        new UiMenuSpecifier().ui {
            menu StatsController.&topCustomerSales2 as MC
            menu StatsController.&listTestInlineEdit as MC
            menuIcon ActionIcon.SHOW, StatsController.&topCustomerSales3 as MC
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
                        diagram(statsService.&buildChart1 as MC) {
                            println "We pass on diagram1"
                            label 'Sales1'
                            boolean showMonthlyGraph = params.boolean('showMonthlyGraph1')
                            boolean groupPerMonth = params.boolean('groupPerMonth1')

                            if (showMonthlyGraph) {
                                menu 'Yearly', StatsController.&topCustomerSales2 as MC, [showMonthlyGraph1: 'false']
                                if (!groupPerMonth) menu 'Group by month', StatsController.&topCustomerSales2 as MC, [showMonthlyGraph1: 'true'] + [groupPerMonth1: 'true']
                                else menu 'Ungroup', StatsController.&topCustomerSales2 as MC, [showMonthlyGraph1: 'true'] + [groupPerMonth1: 'false']
                            } else {
                                menu 'Monthly', StatsController.&topCustomerSales2 as MC, [showMonthlyGraph1: 'true']
                            }
                        }

                    }
                }
                poke p2, {
                    println "We pass on poke2"
                    col BlockSpec.Width.HALF, {
                        println "We pass on col2"
                        diagram(statsService::buildChart2 as MC) {
                            println "We pass on diagram2"
                            label 'Sales2'
                            boolean showMonthlyGraph = params.boolean('showMonthlyGraph2')
                            boolean groupPerMonth = params.boolean('groupPerMonth2')
                            if (showMonthlyGraph) {
                                menu 'Yearly', StatsController.&topCustomerSales2 as MC, [showMonthlyGraph2: 'false']
                                if (!groupPerMonth) menu 'Group by month', StatsController.&topCustomerSales2 as MC, [showMonthlyGraph2: 'true'] + [groupPerMonth2: 'true']
                                else menu 'Ungroup', StatsController.&topCustomerSales2 as MC, [showMonthlyGraph2: 'true'] + [groupPerMonth2: 'false']
                            } else {
                                menu 'Monthly', StatsController.&topCustomerSales2 as MC, [showMonthlyGraph2: 'true']
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
                            diagram(statsService.&buildChart1 as MC) {
                                println "We pass on diagram1"
                                label 'Sales1'
                                boolean showMonthlyGraph = params.boolean('showMonthlyGraph1')
                                boolean groupPerMonth = params.boolean('groupPerMonth1')
                                if (showMonthlyGraph) {
                                    menu 'Yearly', StatsController.&topCustomerSales3 as MC, [showMonthlyGraph1: 'false', v: 0]
                                    if (!groupPerMonth) menu 'Group by month', StatsController.&topCustomerSales2 as MC, [showMonthlyGraph1: 'true', v: 0, groupPerMonth1: 'true']
                                    else menu 'Ungroup', StatsController.&topCustomerSales3 as MC, [showMonthlyGraph1: 'true', v: 0, groupPerMonth1: 'false']
                                } else {
                                    menu 'Monthly', StatsController.&topCustomerSales3 as MC, [showMonthlyGraph1: 'true', v: 0]
                                }
                            }

                        }
                    }
                    poke p2, {
                        println "We pass on poke2"
                        col BlockSpec.Width.HALF, {
                            println "We pass on col2"
                            diagram(statsService::buildChart2 as MC) {
                                println "We pass on diagram2"
                                label 'Sales2'
                                boolean showMonthlyGraph = params.boolean('showMonthlyGraph2')
                                boolean groupPerMonth = params.boolean('groupPerMonth2')
                                if (showMonthlyGraph) {
                                    menu 'Yearly', StatsController.&topCustomerSales3 as MC, [showMonthlyGraph2: 'false', v: 0]
                                    if (!groupPerMonth) menu 'Group by month', StatsController.&topCustomerSales2 as MC, [showMonthlyGraph2: 'true', v: 0, groupPerMonth2: 'true']
                                    else menu 'Ungroup', StatsController.&topCustomerSales3 as MC, [showMonthlyGraph2: 'true', v: 0, groupPerMonth2: 'false']
                                } else {
                                    menu 'Monthly', StatsController.&topCustomerSales3 as MC, [showMonthlyGraph2: 'true', v: 0]
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

    def editTestInlineEdit(TestInlineEdit testInlineEdit) {
        testInlineEdit ?= new TestInlineEdit()
        taackUiService.show createModal {
            form(createForm(testInlineEdit, {
                section {
                    field testInlineEdit.name_
                    field testInlineEdit.age_
                    field testInlineEdit.city_
                }
                formAction(this.&apply as MC)
            }))
        }
    }

    List<TestInlineEdit> testInlineEditList = []
    TaackSaveService taackSaveService

    def apply() {
        println "$params"
        Integer id = params.int('id')
        if (id != null) {
            TestInlineEdit testInlineEdit = id != null ? testInlineEditList[id] : new TestInlineEdit()
            println testInlineEdit
            Map ks = [include: params.keySet()]
            bindData(testInlineEdit, params, ks)
            println testInlineEdit
            if (testInlineEdit.validate()) {
                taackUiService.ajaxReload()
            } else
                taackSaveService.reloadOrRenderErrors(testInlineEdit)
        } else {
            TestInlineEdit testInlineEdit = new TestInlineEdit()
            bindData(testInlineEdit, params, [include: params.keySet()])
            if (testInlineEdit.validate()) {
                testInlineEditList << testInlineEdit
                println testInlineEditList
                taackUiService.ajaxReload()
            } else
                taackSaveService.reloadOrRenderErrors(testInlineEdit)

        }
    }

    def listTestInlineEdit() {
        println "params: ${params}"
        taackUiService.show(new UiBlockSpecifier().ui {
            ajaxBlock {
                table createTable {
                    header {
                        label 'Name'
                        label 'Age'
                        label 'City'
                    }
                    testInlineEditList.eachWithIndex { t, i ->
                        row {
                            rowField t.name_
                            rowQuickEdit this.&apply as MC, i, {
                                rowFieldEdit t.age_
                                rowFieldEdit t.city_
                            }
                        }
                    }
                }, {
                    menuIcon ActionIcon.ADD, StatsController.&editTestInlineEdit as MC
                }
            }
        }, buildMenu())
    }
}

