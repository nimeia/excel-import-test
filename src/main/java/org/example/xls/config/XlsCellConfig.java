package org.example.xls.config;

import org.example.vo.XlsCell;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class XlsCellConfig {
    /**
     * field 的类型
     */
    private Class<?> fieldTypeClass;
    private Class<?> bindClass;
    private String bindField;
    private String styleMethod;
    private String columnStyleMethod;
    private String[] headTitle;
    private int index;

    private Class<?> toClass;

    private String toField;

    /**
     * set method
     */
    private Method setMethod;
    /**
     * get method
     */
    private Method getMethod;

    /**
     * 属性field
     */
    private Field targetField;
    /**
     * set method
     */
    private Method targetSetMethod;
    /**
     * get method
     */
    private Method targetGetMethod;

    /**
     * 嵌套类对象
     */
    private Class<?> innerSheetToClass;

    private String innerSheetToField;

    /**
     * 嵌套类最大条数
     */
    private int innerSheetRowCount;

    /**
     * 属性field
     */
    private Field innerSheetField;
    /**
     * set method
     */
    private Method innerSheetSetMethod;
    /**
     * get method
     */
    private Method innerSheetGetMethod;

    /**
     * 属性field
     */
    private Field innerSheetTargetField;
    /**
     * set method
     */
    private Method innerSheetTargetSetMethod;
    /**
     * get method
     */
    private Method innerSheetTargetGetMethod;

    /**
     * 嵌套类序号
     */
    private int innerSheetIndex;


    public XlsCellConfig(XlsCell xlsCell) {
        this.bindClass = xlsCell.bindClass();
        this.bindField = xlsCell.bindField();
        this.styleMethod = xlsCell.styleMethod();
        this.columnStyleMethod = xlsCell.columnStyleMethod();
        this.headTitle = xlsCell.headTitle();
        this.index = xlsCell.index();
        this.toClass = xlsCell.toClass();
        this.toField = xlsCell.toField();
        this.innerSheetToClass = xlsCell.innerSheetToClass();
        this.innerSheetRowCount = xlsCell.innerSheetRowCount();
        this.innerSheetToField = xlsCell.innerSheetToField();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XlsCellConfig that = (XlsCellConfig) o;
        return Objects.equals(bindClass, that.bindClass) && Objects.equals(bindField, that.bindField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bindClass, bindField);
    }

    public Class<?> fieldTypeClass() {
        return fieldTypeClass;
    }

    public XlsCellConfig fieldTypeClass(Class<?> fieldTypeClass) {
        this.fieldTypeClass = fieldTypeClass;
        return this;
    }

    public Class<?> bindClass() {
        return bindClass;
    }

    public XlsCellConfig bindClass(Class<?> bindClass) {
        this.bindClass = bindClass;
        return this;
    }

    public String bindField() {
        return bindField;
    }

    public XlsCellConfig bindField(String bindField) {
        this.bindField = bindField;
        return this;
    }

    public String styleMethod() {
        return styleMethod;
    }

    public XlsCellConfig styleMethod(String styleMethod) {
        this.styleMethod = styleMethod;
        return this;
    }

    public String columnStyleMethod() {
        return columnStyleMethod;
    }

    public XlsCellConfig columnStyleMethod(String columnStyleMethod) {
        this.columnStyleMethod = columnStyleMethod;
        return this;
    }

    public String[] headTitle() {
        return headTitle;
    }

    public XlsCellConfig headTitle(String[] headTitle) {
        this.headTitle = headTitle;
        return this;
    }

    public int index() {
        return index;
    }

    public XlsCellConfig index(int index) {
        this.index = index;
        return this;
    }

    public Class<?> toClass() {
        return toClass;
    }

    public XlsCellConfig toClass(Class<?> toClass) {
        this.toClass = toClass;
        return this;
    }

    public String toField() {
        return toField;
    }

    public XlsCellConfig toField(String toField) {
        this.toField = toField;
        return this;
    }

    public Method setMethod() {
        return setMethod;
    }

    public XlsCellConfig setMethod(Method setMethod) {
        this.setMethod = setMethod;
        return this;
    }

    public Method getMethod() {
        return getMethod;
    }

    public XlsCellConfig getMethod(Method getMethod) {
        this.getMethod = getMethod;
        return this;
    }

    public Field targetField() {
        return targetField;
    }

    public XlsCellConfig targetField(Field targetField) {
        this.targetField = targetField;
        return this;
    }

    public Method targetSetMethod() {
        return targetSetMethod;
    }

    public XlsCellConfig targetSetMethod(Method targetSetMethod) {
        this.targetSetMethod = targetSetMethod;
        return this;
    }

    public Method targetGetMethod() {
        return targetGetMethod;
    }

    public XlsCellConfig targetGetMethod(Method targetGetMethod) {
        this.targetGetMethod = targetGetMethod;
        return this;
    }

    public Class<?> innerSheetToClass() {
        return innerSheetToClass;
    }

    public XlsCellConfig innerSheetToClass(Class<?> innerSheetToClass) {
        this.innerSheetToClass = innerSheetToClass;
        return this;
    }

    public String innerSheetToField() {
        return innerSheetToField;
    }

    public XlsCellConfig innerSheetToField(String innerSheetToField) {
        this.innerSheetToField = innerSheetToField;
        return this;
    }

    public int innerSheetRowCount() {
        return innerSheetRowCount;
    }

    public XlsCellConfig innerSheetRowCount(int innerSheetRowCount) {
        this.innerSheetRowCount = innerSheetRowCount;
        return this;
    }

    public Field innerSheetField() {
        return innerSheetField;
    }

    public XlsCellConfig innerSheetField(Field innerSheetField) {
        this.innerSheetField = innerSheetField;
        return this;
    }

    public Method innerSheetSetMethod() {
        return innerSheetSetMethod;
    }

    public XlsCellConfig innerSheetSetMethod(Method innerSheetSetMethod) {
        this.innerSheetSetMethod = innerSheetSetMethod;
        return this;
    }

    public Method innerSheetGetMethod() {
        return innerSheetGetMethod;
    }

    public XlsCellConfig innerSheetGetMethod(Method innerSheetGetMethod) {
        this.innerSheetGetMethod = innerSheetGetMethod;
        return this;
    }

    public Field innerSheetTargetField() {
        return innerSheetTargetField;
    }

    public XlsCellConfig innerSheetTargetField(Field innerSheetTargetField) {
        this.innerSheetTargetField = innerSheetTargetField;
        return this;
    }

    public Method innerSheetTargetSetMethod() {
        return innerSheetTargetSetMethod;
    }

    public XlsCellConfig innerSheetTargetSetMethod(Method innerSheetTargetSetMethod) {
        this.innerSheetTargetSetMethod = innerSheetTargetSetMethod;
        return this;
    }

    public Method innerSheetTargetGetMethod() {
        return innerSheetTargetGetMethod;
    }

    public XlsCellConfig innerSheetTargetGetMethod(Method innerSheetTargetGetMethod) {
        this.innerSheetTargetGetMethod = innerSheetTargetGetMethod;
        return this;
    }

    public int innerSheetIndex() {
        return innerSheetIndex;
    }

    public XlsCellConfig innerSheetIndex(int innerSheetIndex) {
        this.innerSheetIndex = innerSheetIndex;
        return this;
    }
}
