package org.example.vo;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XlsSheet {

    Class<?> bindClass() default void.class;

    Class<?> ownerClass() default void.class;

    String bindField() default "";

    boolean isCollection() default false;

    String key() default "sheet";

    String title() default "";

    boolean sheetActive() default false;

    int headRow() default 1;

    /**
     * excel 中显示位置
     *
     * @return
     */
    int index() default -1;

    /**
     * 是否隐藏
     *
     * @return
     */
    boolean hidden() default false;
}
