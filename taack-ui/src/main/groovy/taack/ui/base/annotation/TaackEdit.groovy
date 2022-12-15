package taack.ui.base.annotation

import java.lang.annotation.*

@Repeatable(TaackEdits.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface TaackEdit {
    Class editedClass()
    String editedFields() default ""
}
