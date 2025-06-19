package taack.ui.test

import attachment.Attachment
import attachment.TestCountry
import attachment.config.AttachmentContentType
import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.runtime.MethodClosure
import taack.config.Country
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiFilterSpecifier
import taack.ui.dsl.UiFormSpecifier
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.dsl.UiTableSpecifier

@GrailsCompileStatic
@Secured(['ROLE_ADMIN'])
class TestBugController {

    TaackUiService taackUiService

    def bugsForm() {
        Attachment a = new Attachment(active: false, contentTypeEnum: AttachmentContentType.CSV)
        taackUiService.show(new UiBlockSpecifier().ui {
            form(new UiFormSpecifier().ui new Attachment(), {
                section {
                    field a.active_
                    field a.contentTypeEnum_
                    field a.contentTypeCategoryEnum_
                }
                formAction this.&logForm as MethodClosure, 12, [test1: 'test1']
            })
        }, new UiMenuSpecifier().ui {
            label('test form')
        })
    }

    def bugsForm2() {
        TestCountry a = new TestCountry(country: Country.AD)
        taackUiService.show(new UiBlockSpecifier().ui {
            form(new UiFormSpecifier().ui new TestCountry(country: Country.FR), {
                section {
                    field a.country_
                }
                formAction this.&logForm as MethodClosure, 12, [test1: 'test1']
            })
        }, new UiMenuSpecifier().ui {
            label('test form')
        })
    }

    def bugsForm3() {
        Attachment a = new Attachment(active: false, contentTypeEnum: AttachmentContentType.CSV)
        taackUiService.show(new UiBlockSpecifier().ui {
            form(new UiFormSpecifier().ui new Attachment(), {
                section {
                    field a.active_
                    ajaxField a.contentTypeEnum_, this.&logForm as MethodClosure, a.contentTypeCategoryEnum_
                    field a.contentTypeCategoryEnum_
                }
                formAction this.&logForm as MethodClosure, 12, [test1: 'test1']
            })
        }, new UiMenuSpecifier().ui {
            label('test form')
        })
    }

    def logForm() {
        println params
        render 'OK'
    }

    def bugFilter1() {
        TestCountry tc = new TestCountry(country: Country.FR)
        UiFilterSpecifier f = new UiFilterSpecifier().ui TestCountry, {
            section {
                filterField tc.country_
            }
        }
        UiTableSpecifier t = new UiTableSpecifier().ui {
            header {}
            row {}
        }
        taackUiService.show(new UiBlockSpecifier().ui {
            tableFilter f, t
        }, new UiMenuSpecifier().ui {
            label('test filter')
        })
    }
}
