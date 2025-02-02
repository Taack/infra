package stats

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.runtime.MethodClosure
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.dsl.block.BlockSpec

@GrailsCompileStatic
@Secured(['ROLE_ADMIN'])
class StatsController {

    TaackUiService taackUiService

    static UiMenuSpecifier buildMenu() {
        new UiMenuSpecifier().ui {
            menu StatsController.&topCustomerSales as MethodClosure
        }
    }

    def index() {
        redirect(action: 'topCustomerSales')
    }

    def topCustomerSales() {
        taackUiService.show new UiBlockSpecifier().ui {
            boolean showMonthlyGraph = params.boolean('showMonthlyGraph')
            boolean groupPerMonth = params.boolean('groupPerMonth')
            row {
                col BlockSpec.Width.HALF, {
                    ajaxBlock 'theChart', {
                        println 'coucou'
                        diagram buildChart(showMonthlyGraph, groupPerMonth), {
                            label "Sales1"
                            if (showMonthlyGraph) {
                                menu "Yearly", StatsController.&topCustomerSales as MethodClosure, [showMonthlyGraph: 'false'] + [ajaxBlockId: 'theChart'] + [targetAjaxBlockId: 'theChart'] + [isAjax: 'true']
                                if (!groupPerMonth) menu "Group by month", StatsController.&topCustomerSales as MethodClosure, [showMonthlyGraph: 'true'] + [groupPerMonth: 'true'] + [targetAjaxBlockId: 'theChart'] + [ajaxBlockId: 'theChart'] + [isAjax: 'true']
                                else menu "Ungroup", StatsController.&topCustomerSales as MethodClosure,[showMonthlyGraph: 'true'] + [groupPerMonth: 'false'] + [targetAjaxBlockId: 'theChart'] + [ajaxBlockId: 'theChart'] + [isAjax: 'true']
                            } else {
                                menu "Monthly", StatsController.&topCustomerSales as MethodClosure, [showMonthlyGraph: 'true'] + [targetAjaxBlockId: 'theChart'] + [ajaxBlockId: 'theChart'] + [isAjax: 'true']
                            }
                        }
                    }

                }
                col BlockSpec.Width.HALF, {
                    ajaxBlock 'theChart2', {
                        println 'coucou2'
                        diagram buildChart(showMonthlyGraph, groupPerMonth), {
                            label "Sales2"
                            if (showMonthlyGraph) {
                                menu "Yearly", StatsController.&topCustomerSales as MethodClosure, [showMonthlyGraph: 'false'] + [ajaxBlockId: 'theChart2'] + [targetAjaxBlockId: 'theChart2'] + [isAjax: 'true']
                                if (!groupPerMonth) menu "Group by month", StatsController.&topCustomerSales as MethodClosure, [showMonthlyGraph: 'true'] + [groupPerMonth: 'true'] + [targetAjaxBlockId: 'theChart2'] + [ajaxBlockId: 'theChart2'] + [isAjax: 'true']
                                else menu "Ungroup", StatsController.&topCustomerSales as MethodClosure, [showMonthlyGraph: 'true'] + [groupPerMonth: 'false'] + [ajaxBlockId: 'theChart2'] + [targetAjaxBlockId: 'theChart2'] + [isAjax: 'true']
                            } else {
                                menu "Monthly", StatsController.&topCustomerSales as MethodClosure, [showMonthlyGraph: 'true'] + [ajaxBlockId: 'theChart2'] + [targetAjaxBlockId: 'theChart2'] + [isAjax: 'true']
                            }
                        }
                    }
                }
            }
        }, params.boolean('isAjax') ? buildMenu() : null
    }

    private static UiDiagramSpecifier buildChart(boolean showMonthlyGraph, boolean groupPerMonth) {
        List<Integer> years = 2020..2025
        List<Integer> months = 1..12
        Set<String> origins = ['tutu', 'titi']
        UiDiagramSpecifier chart = new UiDiagramSpecifier()

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

