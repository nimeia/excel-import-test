package org.example.dbconfig;

import java.util.List;

public class DbAnnotationConfig {

    private Integer id;

    private String className;

    private List<DbAnnotationMemberConfig> memberConfigList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<DbAnnotationMemberConfig> getMemberConfigList() {
        return memberConfigList;
    }

    public void setMemberConfigList(List<DbAnnotationMemberConfig> memberConfigList) {
        this.memberConfigList = memberConfigList;
    }
}
