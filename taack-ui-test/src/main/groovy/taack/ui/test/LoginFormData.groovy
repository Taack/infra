package taack.ui.test

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import taack.ast.annotation.TaackFieldEnum

@GrailsCompileStatic
@TaackFieldEnum
class LoginFormData implements Validateable {
    String username
    String password

    static constraints = {
        password nullable: false, widget: 'passwd'
    }
}
