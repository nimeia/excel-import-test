package org.example.xls.config;

import org.example.vo.XlsExcel;

import java.util.ArrayList;
import java.util.Arrays;
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

    public List<XlsSheetConfig> getSheetConfigs() {
        return sheetConfigs;
    }

    public Class<?> getBindClass() {
        return bindClass;
    }

    public String getTitle() {
        return title;
    }

    public String[] getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "XlsExcelConfig{" +
                "bindClass=" + bindClass +
                ", title='" + title + '\'' +
                ", category=" + Arrays.toString(category) +
                ", sheetConfigs=" + sheetConfigs +
                '}';
    }
}
