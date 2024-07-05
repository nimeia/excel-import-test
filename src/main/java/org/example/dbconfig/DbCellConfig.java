package org.example.dbconfig;

public class DbCellConfig {

    private Integer id;

    /**
     * 嵌套类的类名
     */
    private String fieldTypeClassName;

    /**
     * 属性的classFullName ex: private "classFullName" fileName
     */
    private String fieldType;

    private String fieldName;

    //------------------------
    private Boolean xlsIgnore;
    private Integer xlsIndex;
    //-------------------------

    private String headStyle = "headStyle";

    private String validation = "";

    private String [] headTitle = new String[]{};

    private Integer index;

    private String innerSheetToClass = "void.class";

    /**
     * 内部类的属性类型
     */
    private String innerSheetFieldType;

    private String innerSheetToField = "";

    private int innerSheetRowCount = 1;

    private String toField = "";

    private String [] dropdown = new String[]{};

    private String dropdownSql = "";

    private String dropSplit = "-";

    private String format = "";

    private Integer columnWeight = -1;

    public String getInnerSheetFieldType() {
        return innerSheetFieldType;
    }

    public void setInnerSheetFieldType(String innerSheetFieldType) {
        this.innerSheetFieldType = innerSheetFieldType;
    }

    public String getFieldTypeClassName() {
        return fieldTypeClassName;
    }

    public void setFieldTypeClassName(String fieldTypeClassName) {
        this.fieldTypeClassName = fieldTypeClassName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Boolean getXlsIgnore() {
        return xlsIgnore;
    }

    public void setXlsIgnore(Boolean xlsIgnore) {
        this.xlsIgnore = xlsIgnore;
    }

    public Integer getXlsIndex() {
        return xlsIndex;
    }

    public void setXlsIndex(Integer xlsIndex) {
        this.xlsIndex = xlsIndex;
    }

    public String getHeadStyle() {
        return headStyle;
    }

    public void setHeadStyle(String headStyle) {
        this.headStyle = headStyle;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String[] getHeadTitle() {
        return headTitle;
    }

    public void setHeadTitle(String[] headTitle) {
        this.headTitle = headTitle;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getInnerSheetToClass() {
        return innerSheetToClass;
    }

    public void setInnerSheetToClass(String innerSheetToClass) {
        this.innerSheetToClass = innerSheetToClass;
    }

    public String getInnerSheetToField() {
        return innerSheetToField;
    }

    public void setInnerSheetToField(String innerSheetToField) {
        this.innerSheetToField = innerSheetToField;
    }

    public int getInnerSheetRowCount() {
        return innerSheetRowCount;
    }

    public void setInnerSheetRowCount(int innerSheetRowCount) {
        this.innerSheetRowCount = innerSheetRowCount;
    }

    public String getToField() {
        return toField;
    }

    public void setToField(String toField) {
        this.toField = toField;
    }

    public String[] getDropdown() {
        return dropdown;
    }

    public void setDropdown(String[] dropdown) {
        this.dropdown = dropdown;
    }

    public String getDropdownSql() {
        return dropdownSql;
    }

    public void setDropdownSql(String dropdownSql) {
        this.dropdownSql = dropdownSql;
    }

    public String getDropSplit() {
        return dropSplit;
    }

    public void setDropSplit(String dropSplit) {
        this.dropSplit = dropSplit;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getColumnWeight() {
        return columnWeight;
    }

    public void setColumnWeight(Integer columnWeight) {
        this.columnWeight = columnWeight;
    }

}
