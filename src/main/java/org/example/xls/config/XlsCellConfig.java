package org.example.xls.config;

import org.example.vo.XlsCell;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class XlsCellConfig {

    private Class<?> cellType;
    private Class<?> bindClass;
    private String bindField;
    private String styleMethod;
    private String columnStyleMethod;
    private String[] headTitle;
    private int index;

    private Class<?> toClass;

    private String toField;
    /**
     * 属性field
     */
    private Field field;
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
        this.cellType = xlsCell.cellType();
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

    public int getInnerSheetIndex() {
        return innerSheetIndex;
    }

    public Field getField() {
        return field;
    }

    public Method getSetMethod() {
        return setMethod;
    }

    public Method getGetMethod() {
        return getMethod;
    }

    public Class<?> getCellType() {
        return cellType;
    }

    public Class<?> getBindClass() {
        return bindClass;
    }

    public String getBindField() {
        return bindField;
    }

    public String getStyleMethod() {
        return styleMethod;
    }

    public String getColumnStyleMethod() {
        return columnStyleMethod;
    }

    public String[] getHeadTitle() {
        return headTitle;
    }

    public int getIndex() {
        return index;
    }

    public Field getTargetField() {
        return targetField;
    }

    public Method getTargetSetMethod() {
        return targetSetMethod;
    }

    public Method getTargetGetMethod() {
        return targetGetMethod;
    }

    public Class<?> getToClass() {
        return toClass;
    }

    public String getToField() {
        return toField;
    }

    public Class<?> getInnerSheetToClass() {
        return innerSheetToClass;
    }

    public int getInnerSheetRowCount() {
        return innerSheetRowCount;
    }

    public Field getInnerSheetField() {
        return innerSheetField;
    }

    public Method getInnerSheetSetMethod() {
        return innerSheetSetMethod;
    }

    public Method getInnerSheetGetMethod() {
        return innerSheetGetMethod;
    }

    public Field getInnerSheetTargetField() {
        return innerSheetTargetField;
    }

    public Method getInnerSheetTargetSetMethod() {
        return innerSheetTargetSetMethod;
    }

    public Method getInnerSheetTargetGetMethod() {
        return innerSheetTargetGetMethod;
    }
}
