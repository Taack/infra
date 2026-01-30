package stats

import lodomain.TestInlineEdit
import grails.compiler.GrailsCompileStatic
import grails.web.api.WebAttributes
import jakarta.annotation.PostConstruct
import org.codehaus.groovy.runtime.MethodClosure as MC
import taack.render.TaackUiService
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.UiTableSpecifier

@GrailsCompileStatic
class StatsService implements WebAttributes {

    @PostConstruct
    void init() {
        TestInlineEdit testInlineEdit = new TestInlineEdit()
        TaackUiService.registerFieldEdit(TestInlineEdit, StatsController.&editTestInlineEdit as MC, StatsController.&apply as MC, testInlineEdit.name_, testInlineEdit.age_)
    }

    UiTableSpecifier buildTable() {
        new UiTableSpecifier().ui {
            header {
                label "col1"
                label "col2"
            }
            row {
                rowColumn {
                    rowAction "v11", StatsController.&topCustomerSales2 as MC, [v: 1, isAjax: true]
                }
                rowField "v12"
            }
            row {
                rowColumn {
                    rowAction "v21", StatsController.&topCustomerSales2 as MC, [v: 2]
                }
                rowField "v32"
            }
        }
    }

    UiDiagramSpecifier buildChart1() {
        buildChart(1)
    }

    UiDiagramSpecifier buildChart2() {
        buildChart(2)
    }

    UiDiagramSpecifier buildChart(int index) {
        List<Integer> years = 2020..2025
        List<Integer> months = 1..12
        Set<String> origins = ['tutu', 'titi']
        UiDiagramSpecifier chart = new UiDiagramSpecifier()
        boolean showMonthlyGraph = params.boolean('showMonthlyGraph' + index)
        boolean groupPerMonth = params.boolean('groupPerMonth' + index)

        println "buildChart called $showMonthlyGraph $groupPerMonth"

        if (showMonthlyGraph) {
            List<String> labelStrings = []
            if (groupPerMonth) {
                months.each { y ->
                    years.each { m ->
                        labelStrings << "$y/$m".toString()
                    }
                    labelStrings << "* $y *".toString()
                }
            } else {
                years.each { y ->
                    months.each { m ->
                        labelStrings << "$m/$y".toString()
                    }
                }
            }
            chart.ui {
                bar(true) {
                    labels labelStrings as String[]
                    origins.sort().each { o ->
                        List<BigDecimal> yDataList = []
                        if (groupPerMonth) {
                            months.each { m ->
                                years.each { yIt ->
                                    yDataList.add(Math.random().toBigDecimal() * 20)
                                }
                                yDataList.add(0.0)
                            }
                        } else {
                            years.each { yIt ->
                                months.each { m ->
                                    yDataList.add(Math.random().toBigDecimal() * 20)
                                }
                            }
                        }
                        dataset o, yDataList as BigDecimal[]
                    }
                }
            }
        } else {
            def rands = []
            years.each {
                rands.add Math.random() * 20 as BigDecimal
            }
            chart.ui {
                bar true, {
                    labels years*.toString() as String[]
                    origins.sort().each { o ->
                        dataset o, rands as BigDecimal[]
                    }
                }
            }
        }
        chart
    }

}
