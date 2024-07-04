package org.example.vo;


import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XlsCell {

    String headStyle() default "headStyle";

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


    /**
     * 下拉固定选项
     *
     */
    String [] dropdown() default {};

    /**
     * 从数据库中获取数据sql
     *
     */
    String dropdownSql() default "";

    /**
     * 选项值 key:value 的分割符
     *
     */
    String dropSplit() default "-";

    String format() default "";

    int columnWeight() default -1;

}
