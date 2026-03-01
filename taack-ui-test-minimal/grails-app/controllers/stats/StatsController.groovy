package stats

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.Validateable
import grails.web.api.WebAttributes
import jakarta.annotation.PostConstruct
import lodomain.TestInlineEdit
import lodomain.TestStatus
import org.codehaus.groovy.runtime.MethodClosure as MC
import taack.render.TaackSaveService
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.block.BlockBase
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.common.ActionIcon
import taack.ui.test.RootController

import static taack.ui.TaackUi.*

@GrailsCompileStatic
class StatsParams implements Validateable {
    Boolean showMonthlyGraph1 = true
    Boolean showMonthlyGraph2 = true
    Boolean groupPerMonth1 = true
    Boolean groupPerMonth2 = true
    Integer v = null

    static constraints = {
        v nullable: true, min: 0, max: 2
    }

    @Override
    String toString() {
        return "StatsParams{" +
                "showMonthlyGraph1=" + showMonthlyGraph1 +
                ", showMonthlyGraph2=" + showMonthlyGraph2 +
                ", groupPerMonth1=" + groupPerMonth1 +
                ", groupPerMonth2=" + groupPerMonth2 +
                ", v=" + v +
                '}'
    }
}

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

    def topCustomerSalesCard(StatsParams statsParams) {

        println params

        boolean p0 = statsParams.v == 0
        boolean p1 = statsParams.v == 1
        boolean p2 = statsParams.v == 2

        BlockBase.debug = false

        taackUiService.show(new UiBlockSpecifier().ui {
            poke p0, { // Needed to refresh links with parameter to keep
                table statsService.buildTable()
            }
            row {
                poke p1, {
                    col BlockSpec.Width.HALF, {
                        card({ // Menu not refresh
                            label 'Sales1'
                            menu 'Yearly', StatsController.&topCustomerSalesCard as MC, new StatsParams(showMonthlyGraph1: false, v: 1)
                            menu 'Monthly', StatsController.&topCustomerSalesCard as MC, new StatsParams(groupPerMonth1: false, v: 1)
                            menu 'Group by month', StatsController.&topCustomerSalesCard as MC, new StatsParams(v: 1)
                        }, {
                            diagram(statsService::buildChart1 as MC)
                        })
                    }
                }
                poke p2, {
                    col BlockSpec.Width.HALF, {
                        diagram(statsService::buildChart2 as MC) {
                            label 'Sales2'
                            menu 'Yearly', StatsController.&topCustomerSalesCard as MC, new StatsParams(showMonthlyGraph2: false)
                            menu 'Group by month', StatsController.&topCustomerSalesCard as MC, new StatsParams(groupPerMonth2: false)
                            menu 'Monthly', StatsController.&topCustomerSalesCard as MC, new StatsParams()
                        }
                    }
                }
            }
            BlockBase.debug = false
        }, RootController.buildMenu(), statsParams)
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
        }, RootController.buildMenu())
    }
}

