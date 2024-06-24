package org.example.xls.config;

import org.example.vo.XlsSheet;

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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XlsSheetConfig that = (XlsSheetConfig) o;
        return isCollection == that.isCollection && sheetActive == that.sheetActive && headRow == that.headRow && index == that.index && hidden == that.hidden && Objects.equals(bindClass, that.bindClass) && Objects.equals(ownerClass, that.ownerClass) && Objects.equals(bindField, that.bindField) && Objects.equals(key, that.key) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bindClass, ownerClass, bindField, isCollection, key, title, sheetActive, headRow, index, hidden);
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
}