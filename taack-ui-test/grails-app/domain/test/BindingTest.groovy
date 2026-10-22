package test

import grails.compiler.GrailsCompileStatic
import taack.ast.annotation.TaackFieldEnum

@TaackFieldEnum
@GrailsCompileStatic
class BindingTest {
    BigDecimal aNumber
    Date aDate

    static constraints = {

    }
}
