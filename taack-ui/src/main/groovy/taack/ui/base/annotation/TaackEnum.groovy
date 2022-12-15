package taack.ui.base.annotation


import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface TaackEnum {
    String name()

    boolean i18n() default false

    boolean returnSelectOption() default false

    String messagePrefix() default ""

    boolean notCached() default false
}
