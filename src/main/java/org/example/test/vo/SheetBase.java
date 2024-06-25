package org.example.test.vo;

import org.example.business.BusinessSheet1;
import org.example.vo.XlsCell;
import org.example.vo.XlsSheet;

import java.math.BigDecimal;
import java.util.Date;

@XlsSheet(index = 1, hidden = false, title = "Sheet 样例2",toClass = BusinessSheet1.class)
public class SheetBase {

    @XlsCell(headTitle = {"组1", "子组2", "三级组2"})
    private Integer id;

    @XlsCell(headTitle = {"组1", "子组2", "三级组2"})
    private String name;

    @XlsCell(headTitle = {"组1", "子组2", "三级组2"})
    private BigDecimal price;

    @XlsCell(index = 3)
    private String email;

    @XlsCell(index = 4)
    private Date birthDate;

    @XlsCell(index = 5)
    private String idCard;

    @XlsCell(headTitle = {"隐藏列"})
    private String hiddenField;

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

    public String getHiddenField() {
        return hiddenField;
    }

    public void setHiddenField(String hiddenField) {
        this.hiddenField = hiddenField;
    }
}
