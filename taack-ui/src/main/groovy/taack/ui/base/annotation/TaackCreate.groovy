package taack.ui.base.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface TaackCreate {
    Class createdBeanClass()
    Class associatedBeanClass() default String
    String parametricUrl() default ""
}