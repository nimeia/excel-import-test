package org.example.xls.config;

import org.example.vo.XlsCell;

import java.util.Arrays;
import java.util.Objects;

public class XlsCellConfig {

    private Class<?> cellType;
    private Class<?> bindClass;
    private String bindField;
    private String styleMethod;
    private String columnStyleMethod;
    private String[] headTitle;
    private int index;

    public XlsCellConfig(XlsCell xlsCell) {
        this.cellType = xlsCell.cellType();
        this.bindClass = xlsCell.bindClass();
        this.bindField = xlsCell.bindField();
        this.styleMethod = xlsCell.styleMethod();
        this.columnStyleMethod = xlsCell.columnStyleMethod();
        this.headTitle = xlsCell.headTitle();
        this.index = xlsCell.index();
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
}
