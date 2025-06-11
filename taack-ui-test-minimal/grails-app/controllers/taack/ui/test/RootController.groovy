package taack.ui.test

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.runtime.MethodClosure as MC
import stats.StatsService
import taack.app.TaackApp
import taack.app.TaackAppRegisterService
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.dsl.UiShowSpecifier
import taack.ui.dsl.block.BlockSpec
/*
TODO: Add an infrastructure to list new stuffs from a user and a timestamp
 */

@GrailsCompileStatic
@Secured(["permitAll"])
class RootController {
    TaackUiService taackUiService
    StatsService statsService

    private static UiMenuSpecifier buildMenu() {
        UiMenuSpecifier m = new UiMenuSpecifier()
        m.ui {
            menu this.&index as MC
        }
        m
    }

    def index() {
        UiBlockSpecifier b = new UiBlockSpecifier().ui {
            diagram this.statsService.buildChart()
            show new UiShowSpecifier().ui {
                field("""couo""")
            }
            row {
                TaackAppRegisterService.apps.eachWithIndex { TaackApp a, int i ->
                    col BlockSpec.Width.FLEX, {
                    }
                }
            }
        }
        taackUiService.show(b, buildMenu())
    }

    def updates() {
        taackUiService.show(new UiBlockSpecifier().ui {
            ajaxBlock "updates", {
                show(new UiShowSpecifier().ui {
                    inlineHtml("WiP", "")
                })
            }
        }, buildMenu())
    }

    def todo() {
        taackUiService.show(new UiBlockSpecifier().ui {
            ajaxBlock "todo", {
                show(new UiShowSpecifier().ui {
                    inlineHtml("WiP", "")
                })
            }
        }, buildMenu())
    }
}
