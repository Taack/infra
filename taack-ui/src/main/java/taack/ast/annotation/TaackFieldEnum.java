package taack.ast.annotation;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;
//import taack.ast.model.TaackFieldEnumASTTransformation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
@GroovyASTTransformationClass(value = {"taack.ast.model.TaackFieldEnumASTTransformation"})
public @interface TaackFieldEnum {
}
