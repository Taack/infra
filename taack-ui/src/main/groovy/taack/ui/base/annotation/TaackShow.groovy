package taack.ui.base.annotation

import java.lang.annotation.*

@Repeatable(TaackShows.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface TaackShow {
    Class showedClass()
    String field() default ""
}
