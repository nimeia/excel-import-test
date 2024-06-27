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

    Class<?> innerSheetToClass() default void.class;

    String innerSheetToField() default "";

    int innerSheetRowCount() default 1;


    /**
     * 属于哪个类
     * @return
     */
    Class<?> toClass() default void.class;

    /**
     * 属于哪个类的哪个属性
     * @return
     */
    String toField() default "";

}
