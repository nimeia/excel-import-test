package org.example.test.vo;

import org.example.business.CourseBusiness;
import org.example.business.StudentBusiness;
import org.example.vo.XlsCell;
import org.example.vo.XlsSheet;

import java.math.BigDecimal;
import java.util.Date;

@XlsSheet(index = 1, hidden = false, title = "学生", toClass = StudentBusiness.class)
public class Student {

    @XlsCell(headTitle = {"用户", "ID"},index = 1,validation = "Integer")
    private Integer id;

    @XlsCell(headTitle = {"用户", "名称"},index = 2)
    private String name;

    @XlsCell(headTitle = {"金额"},index = 3)
    private BigDecimal price;

    @XlsCell(headTitle = {"主表ID"}, index = 4)
    private Integer parentId;

    @XlsCell(index = 5)
    private String email;

    @XlsCell(index = 6)
    private Date birthDate;

    @XlsCell(index = 7)
    private String idCard;

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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
