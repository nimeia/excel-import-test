package org.example.test.vo;

import org.example.vo.XlsCell;

import java.math.BigDecimal;


public class Course {

    @XlsCell(index = 1)
    private Integer innerId;

    @XlsCell(index = 2,toField = "name")
    private String courseName;

    @XlsCell(index = 3, headTitle = {"价格"},toField = "innerPrice")
    private BigDecimal coursePrice;

    public Integer getInnerId() {
        return innerId;
    }

    public void setInnerId(Integer innerId) {
        this.innerId = innerId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public BigDecimal getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(BigDecimal coursePrice) {
        this.coursePrice = coursePrice;
    }
}
