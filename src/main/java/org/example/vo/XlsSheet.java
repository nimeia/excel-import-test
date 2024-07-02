package org.example.vo;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XlsSheet {

    /**
     * sheet 标题,excel 下方的名称
     * 
     */
    String title() default "";

    /**
     * 是否默认激活
     * 
     */
    boolean sheetActive() default false;

    /**
     * 标题行数，会自动计算
     * 
     */
    int headRow() default 1;

    /**
     * excel 中显示位置
     *
     * 
     */
    int index() ;

    /**
     * 是否隐藏
     *
     * 
     */
    boolean hidden() default false;

    /**
     * 填充目标对象
     *
     * 
     */
    Class<?> toClass();

    /**
     * 指向父对象
     * 
     */
    Class<?> parentClass() default void.class;

    /**
     * 父对象中的容器对象
     * 
     */
    String parentContainerField() default "";

    /**
     * 父级关联ID
     * 
     */
    String parentLinkId() default "";

    /**
     * 关联ID
     * 
     */
    String linkId() default "";

}
