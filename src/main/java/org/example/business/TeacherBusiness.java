package org.example.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TeacherBusiness {
    private Integer id;

    private String name;

    private BigDecimal price;

    private String businessEmail;

    private Date birthDate;

    private String businessIdCard;

    private List<CourseBusiness> courseBusiness;

    private List<StudentBusiness> studentBusiness;

    public List<StudentBusiness> getStudentBusiness() {
        return studentBusiness;
    }

    public void setStudentBusiness(List<StudentBusiness> studentBusiness) {
        this.studentBusiness = studentBusiness;
    }

    public List<CourseBusiness> getBusinessInnerSheet() {
        return courseBusiness;
    }

    public void setBusinessInnerSheet(List<CourseBusiness> courseBusiness) {
        this.courseBusiness = courseBusiness;
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

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getBusinessIdCard() {
        return businessIdCard;
    }

    public void setBusinessIdCard(String businessIdCard) {
        this.businessIdCard = businessIdCard;
    }

}
