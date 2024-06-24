package org.example.vo;


import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XlsCell {

    Class<?> cellType() default void.class;

    Class<?> bindClass() default void.class;

    String bindField() default "";

    String styleMethod() default "";

    String columnStyleMethod() default "";

    String[] headTitle() default {};

    int index() default -1;

}
