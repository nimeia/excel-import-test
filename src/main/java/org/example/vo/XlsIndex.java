package org.example.vo;

import java.lang.annotation.*;

/**
 * 用于指定sheet 的顺序
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XlsIndex {

    int index();
}
