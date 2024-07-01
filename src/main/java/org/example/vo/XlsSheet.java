package org.example.vo;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XlsSheet {

    /**
     * sheet 标题,excel 下方的名称
     * @return
     */
    String title() default "";

    /**
     * 是否默认激活
     * @return
     */
    boolean sheetActive() default false;

    /**
     * 标题行数，会自动计算
     * @return
     */
    int headRow() default 1;

    /**
     * excel 中显示位置
     *
     * @return
     */
    int index() ;

    /**
     * 是否隐藏
     *
     * @return
     */
    boolean hidden() default false;

    /**
     * 填充目标对象
     *
     * @return
     */
    Class<?> toClass() default void.class;

    /**
     * 是否按字段名，默认转换
     * @return
     */
    boolean fillByFiledName() default true;
}
