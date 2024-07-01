package org.example.vo;


import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XlsCell {

    String headStyle() default "";

    String validation() default "";

    String[] headTitle() default {};

    int index() ;

    Class<?> innerSheetToClass() default void.class;

    String innerSheetToField() default "";

    int innerSheetRowCount() default 1;

    /**
     * 属于哪个类的哪个属性
     *
     * @return
     */
    String toField() default "";

}
