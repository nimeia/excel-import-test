package org.example.test.vo;

import org.example.business.BusinessSheet1;
import org.example.vo.XlsCell;
import org.example.vo.XlsSheet;

import java.math.BigDecimal;
import java.util.Date;

@XlsSheet(index = 1, hidden = false, title = "Sheet 样例1", toClass = BusinessSheet1.class)
public class Sheet1  {

    @XlsCell(index = 1, headTitle = {"组1", "子组2", "三级组1"}, headStyle = "headStyle")
    private Integer id;

    @XlsCell(index = 2, headTitle = {"组1", "子组2", "三级组2"}, headStyle = "headStyle")
    private String name;

    @XlsCell(index = 6, headTitle = {"组1", "子组2", "三级组2"}, headStyle = "headStyle")
    private BigDecimal price;

    @XlsCell(index = 3, headStyle = "headStyle")
    private String email;

    @XlsCell(index = 4, headStyle = "headStyle", validation = "dateStyle")
    private Date birthDate;

    @XlsCell(index = 5, headStyle = "headStyle")
    private String idCard;

    @XlsCell(index = 7, headTitle = {"隐藏列"}, headStyle = "headStyle")
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
