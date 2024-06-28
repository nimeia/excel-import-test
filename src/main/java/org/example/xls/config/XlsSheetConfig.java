package org.example.xls.config;

import org.example.vo.XlsSheet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class XlsSheetConfig {

    private Class<?> bindClass;
    private Class<?> ownerClass;
    private String bindField;
    private boolean isCollection;
    private String key;
    private String title;
    private boolean sheetActive;
    private int headRow;

    /**
     * 填充目标对象
     *
     * @return
     */
    private Class<?> toClass;

    private boolean fillByFiledName;
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
     * excel 中显示位置
     *
     * @return
     */
    private int index;


    /**
     * 是否隐藏
     *
     * @return
     */
    private boolean hidden;

    private List<XlsCellConfig> xlsCellConfigs = new ArrayList<XlsCellConfig>();

    public XlsSheetConfig(XlsSheet xlsSheet) {
        this.bindClass = xlsSheet.getClass();
        this.ownerClass = xlsSheet.getClass();
        this.bindField = xlsSheet.bindField();
        this.isCollection = xlsSheet.isCollection();
        this.key = xlsSheet.key();
        this.title = xlsSheet.title();
        this.sheetActive = xlsSheet.sheetActive();
        this.headRow = xlsSheet.headRow();
        this.index = xlsSheet.index();
        this.hidden = xlsSheet.hidden();
        this.toClass = xlsSheet.toClass();
        this.fillByFiledName = xlsSheet.fillByFiledName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XlsSheetConfig that = (XlsSheetConfig) o;
        return Objects.equals(bindClass, that.bindClass) && Objects.equals(ownerClass, that.ownerClass) && Objects.equals(bindField, that.bindField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bindClass, ownerClass, bindField);
    }

    public List<XlsCellConfig> getXlsCellConfigs() {
        return xlsCellConfigs;
    }

    public Class<?> getToClass() {
        return toClass;
    }

    public boolean isFillByFiledName() {
        return fillByFiledName;
    }

    public Class<?> getBindClass() {
        return bindClass;
    }

    public Class<?> getOwnerClass() {
        return ownerClass;
    }

    public String getBindField() {
        return bindField;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSheetActive() {
        return sheetActive;
    }

    public int getHeadRow() {
        return headRow;
    }

    public int getIndex() {
        return index;
    }

    public boolean isHidden() {
        return hidden;
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

    @Override
    public String toString() {
        return "XlsSheetConfig{" +
                "bindClass=" + bindClass +
                ", ownerClass=" + ownerClass +
                ", bindField='" + bindField + '\'' +
                ", isCollection=" + isCollection +
                ", key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", sheetActive=" + sheetActive +
                ", headRow=" + headRow +
                ", toClass=" + toClass +
                ", fillByFiledName=" + fillByFiledName +
                ", field=" + field +
                ", setMethod=" + setMethod +
                ", getMethod=" + getMethod +
                ", index=" + index +
                ", hidden=" + hidden +
                ", xlsCellConfigs=" + xlsCellConfigs +
                '}';
    }
}
