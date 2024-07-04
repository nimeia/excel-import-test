package org.example.dbconfig;

import java.util.List;

public class DbExcelConfig {

    private String packageName;

    private String className ;

    private String title = "";

    private String [] category = new String[]{};
    private List<DbSheetConfig> dbSheetConfigs;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getCategory() {
        return category;
    }

    public void setCategory(String[] category) {
        this.category = category;
    }

    public void setDbSheetConfigs(List<DbSheetConfig> dbSheetConfigs) {
        this.dbSheetConfigs = dbSheetConfigs;
    }

    public List<DbSheetConfig> getDbSheetConfigs() {
        return dbSheetConfigs;
    }
}
