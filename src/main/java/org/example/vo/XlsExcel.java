package org.example.vo;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XlsExcel {

    Class<?> bindClass() default void.class;

    /**
     * 文件引出时使用
     */
    String title() default "";


    /**
     * 格式为 "type|key|display"
     */
    String[] category();
}
