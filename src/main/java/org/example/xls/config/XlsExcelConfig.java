package org.example.xls.config;

import org.example.vo.XlsExcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class XlsExcelConfig {

    /**
     * 绑定 class
     */
    private Class<?> bindClass;
    /**
     * 文件引出时使用
     */
    private String title;
    /**
     * 格式为 "type|key|display"
     */
    private String[] category;

    private List<XlsSheetConfig> sheetConfigs = new ArrayList<>();

    public XlsExcelConfig(XlsExcel xlsExcel) {
        this.title = xlsExcel.title();
        this.category = xlsExcel.category();
        this.bindClass = xlsExcel.bindClass();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XlsExcelConfig that = (XlsExcelConfig) o;
        return Objects.equals(bindClass, that.bindClass);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bindClass);
    }

    public Class<?> bindClass() {
        return bindClass;
    }

    public XlsExcelConfig bindClass(Class<?> bindClass) {
        this.bindClass = bindClass;
        return this;
    }

    public String title() {
        return title;
    }

    public XlsExcelConfig title(String title) {
        this.title = title;
        return this;
    }

    public String[] category() {
        return category;
    }

    public XlsExcelConfig category(String[] category) {
        this.category = category;
        return this;
    }

    public List<XlsSheetConfig> sheetConfigs() {
        return sheetConfigs;
    }

    public XlsExcelConfig sheetConfigs(List<XlsSheetConfig> sheetConfigs) {
        this.sheetConfigs = sheetConfigs;
        return this;
    }
}
