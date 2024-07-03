package org.example.service;

import java.lang.annotation.Annotation;

public interface XlsAnnotationParseService<T extends Annotation> {

    /**
     * 返回需要处理annotation 类型
     */
    public  Class<T> getAnnotation();

    public void parse(T annotation ,Object annotationConfigObj);
}
