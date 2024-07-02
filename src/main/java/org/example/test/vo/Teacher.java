package org.example.test.vo;

import org.example.business.CourseBusiness;
import org.example.business.TeacherBusiness;
import org.example.vo.XlsCell;
import org.example.vo.XlsSheet;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@XlsSheet(index = 1, hidden = false, title = "老师", toClass = TeacherBusiness.class)
public class Teacher {

    @XlsCell(index = 1, headTitle = {"老师信息", "ID"}, headStyle = "redHead")
    private Integer id;

    @XlsCell(index = 2, headTitle = {"老师信息", "名称"})
    private String name;

    @XlsCell(index = 6, headTitle = {"price"})
    private BigDecimal price;

    @XlsCell(index = 3,validation = "email")
    private String email;

    @XlsCell(index = 4)
    private Date birthDate;

    @XlsCell(index = 5)
    private String idCard;

    @XlsCell(index = 7,innerSheetRowCount = 3,innerSheetToClass = CourseBusiness.class,toField = "courseBusiness")
    private List<Course> courses;

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

}
