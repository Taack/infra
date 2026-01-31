package lodomain

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import taack.ast.annotation.TaackFieldEnum

@TaackFieldEnum
@GrailsCompileStatic
class TestInlineEdit implements Validateable {
    String name
    Integer age
    String city

    static constraints = {
        city nullable: true
        age validator: { Integer i, TestInlineEdit o ->
            if (i < 18) "age.too.low"
            else if (i > 38) "age.too.high"
        }
    }


    @Override
    String toString() {
        return "TestInlineEdit{" +
                "grails_validation_Validateable__beforeValidateHelper=" + grails_validation_Validateable__beforeValidateHelper +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", city='" + city + '\'' +
                ", grails_validation_Validateable__errors=" + grails_validation_Validateable__errors +
                '}';
    }
}
