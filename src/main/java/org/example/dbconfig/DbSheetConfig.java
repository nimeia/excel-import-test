package org.example.dbconfig;

import java.util.List;

public class DbSheetConfig {

    private String className ;

    /**
     * class 全路径名称
     */
    private String toClass;

    private String title;

    private Boolean sheetActive = false;

    private Integer headRow = 1;

    private Integer index;

    private Boolean hidden = false;

    /**
     * class 全路径名称
     */
    private String parentClass = "void.class";

    private String parentContainerField;

    private String parentLinkId;

    private String linkId;
    private List<DbCellConfig> dbCellConfigs;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getToClass() {
        return toClass;
    }

    public void setToClass(String toClass) {
        this.toClass = toClass;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getSheetActive() {
        return sheetActive;
    }

    public void setSheetActive(Boolean sheetActive) {
        this.sheetActive = sheetActive;
    }

    public Integer getHeadRow() {
        return headRow;
    }

    public void setHeadRow(Integer headRow) {
        this.headRow = headRow;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getParentClass() {
        return parentClass;
    }

    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    public String getParentContainerField() {
        return parentContainerField;
    }

    public void setParentContainerField(String parentContainerField) {
        this.parentContainerField = parentContainerField;
    }

    public String getParentLinkId() {
        return parentLinkId;
    }

    public void setParentLinkId(String parentLinkId) {
        this.parentLinkId = parentLinkId;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public void setDbCellConfigs(List<DbCellConfig> dbCellConfigs) {
        this.dbCellConfigs = dbCellConfigs;
    }

    public List<DbCellConfig> getDbCellConfigs() {
        return dbCellConfigs;
    }
}
