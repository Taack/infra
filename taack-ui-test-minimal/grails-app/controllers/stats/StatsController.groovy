package stats


import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import grails.web.api.WebAttributes
import jakarta.annotation.PostConstruct
import lodomain.TestInlineEdit
import lodomain.TestStatus
import org.codehaus.groovy.runtime.MethodClosure as MC
import taack.render.TaackSaveService
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.dsl.block.BlockBase
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.common.ActionIcon

import static taack.ui.TaackUi.*

@GrailsCompileStatic
@Secured(['permitAll'])
class StatsController implements WebAttributes {

    TaackUiService taackUiService
    StatsService statsService
    List<TestInlineEdit> testInlineEditList = []
    TaackSaveService taackSaveService

    @PostConstruct
    void init() {

        Calendar calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 100)
        testInlineEditList.add new TestInlineEdit(name: 'Higgins', age: 58, city: 'Kronenburg', birthday: Date.from(calendar.toInstant()), status: TestStatus.NEW)
        calendar.add(Calendar.DAY_OF_YEAR, 67)
        testInlineEditList.add new TestInlineEdit(name: 'Sgt Miclin', age: 68, city: 'Hamburger', birthday: Date.from(calendar.toInstant()), status: TestStatus.NEW)
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        testInlineEditList.add new TestInlineEdit(name: 'Magnum', age: 68, city: 'Hawaii', birthday: Date.from(calendar.toInstant()), status: TestStatus.NEW)
    }

    static UiMenuSpecifier buildMenu() {
        new UiMenuSpecifier().ui {
            menu StatsController.&topCustomerSalesCard as MC
            menu StatsController.&listTestInlineEdit as MC
        }
    }

    def index() {
        redirect(action: 'topCustomerSalesCard')
    }
    def topCustomerSalesCard() {
        boolean p0 = params.int('v') == 0
        boolean p1 = params.int('v') == 1
        boolean p2 = params.int('v') == 2

        println "p0: $p0, p1: $p1, p2: $p2 $params"
        BlockBase.debug = true

        taackUiService.show(new UiBlockSpecifier().ui {
                poke p0, { // Needed to refresh links with parameter to keep
                    table statsService.buildTable()
                }

                row {
                    poke p1, {
                        col BlockSpec.Width.HALF, {
                            card( { // Menu not refresh
                                label 'Sales1'
                                menu 'Yearly', StatsController.&topCustomerSalesCard as MC, [showMonthlyGraph1: 'false', v: 0]
                                menu 'Monthly', StatsController.&topCustomerSalesCard as MC, [showMonthlyGraph1: 'true', v: 0, groupPerMonth1: 'false']
                                menu 'Group by month', StatsController.&topCustomerSalesCard as MC, [showMonthlyGraph1: 'true', v: 0, groupPerMonth1: 'true']

                            }, {
                                diagram(statsService::buildChart1 as MC)
                            })
                        }
                    }
                    poke p2, {
                        col BlockSpec.Width.HALF, {
                            diagram(statsService::buildChart2 as MC) {
                                label 'Sales2'
                                    menu 'Yearly', StatsController.&topCustomerSalesCard as MC, [showMonthlyGraph2: 'false', v: 0, groupPerMonth2: 'false']
                                    menu 'Group by month', StatsController.&topCustomerSalesCard as MC, [showMonthlyGraph2: 'true', v: 0, groupPerMonth2: 'true']
                                    menu 'Monthly', StatsController.&topCustomerSalesCard as MC, [showMonthlyGraph2: 'true', v: 0, groupPerMonth2: 'false']
                            }
                        }
                    }
                }
            BlockBase.debug = false
        }, buildMenu(),'showMonthlyGraph1', 'groupPerMonth1', 'showMonthlyGraph2', 'groupPerMonth2')
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

    def apply() {
        Integer id = params.int('id')
        if (id != null) {
            TestInlineEdit testInlineEdit = id != null ? testInlineEditList[id] : new TestInlineEdit()
            Map ks = [include: params.keySet()]
            bindData(testInlineEdit, params, ks)
            if (testInlineEdit.validate()) {
                taackUiService.ajaxReload()
            } else
                taackSaveService.reloadOrRenderErrors(testInlineEdit)
        } else {
            TestInlineEdit testInlineEdit = new TestInlineEdit()
            bindData(testInlineEdit, params, [include: params.keySet()])
            if (testInlineEdit.validate()) {
                testInlineEditList << testInlineEdit
                taackUiService.ajaxReload()
            } else
                taackSaveService.reloadOrRenderErrors(testInlineEdit)

        }
    }

    def listTestInlineEdit() {
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
                                rowFieldEdit t.birthday_
                                rowFieldEdit t.status_
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

