package org.example.test.vo;

import org.example.business.BusinessSheet2;
import org.example.vo.XlsCell;
import org.example.vo.XlsSheet;
import org.example.xls.config.XlsStyleConfig;

import java.math.BigDecimal;
import java.util.Date;

@XlsSheet(index = 1, hidden = false, title = "Sheet 样例3", toClass = BusinessSheet2.class)
public class Sheet2 extends XlsStyleConfig {

    @XlsCell(headTitle = {"组1", "子组2", "三级组1"},index = 1, styleMethod = "headStyle")
    private Integer id;

    @XlsCell(headTitle = {"组1", "子组2", "三级组2"},index = 2, styleMethod = "headStyle")
    private String name;

    @XlsCell(headTitle = {"组1", "子组2", "三级组3"},index = 3, styleMethod = "headStyle")
    private BigDecimal price;

    @XlsCell(headTitle = {"主表ID"}, index = 4, styleMethod = "headStyle")
    private Integer parentId;

    @XlsCell(index = 5, styleMethod = "headStyle")
    private String email;

    @XlsCell(index = 6, styleMethod = "headStyle", columnStyleMethod = "dateStyle")
    private Date birthDate;

    @XlsCell(index = 7, styleMethod = "headStyle")
    private String idCard;

    @XlsCell(headTitle = {"隐藏列"}, styleMethod = "headStyle",index = 8)
    private String hiddenField;

    @XlsCell(index = 9, styleMethod = "headStyle", innerSheetRowCount = 3)
    private InnerSheet innerSheet;

    public InnerSheet getInnerSheet() {
        return innerSheet;
    }

    public void setInnerSheet(InnerSheet innerSheet) {
        this.innerSheet = innerSheet;
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

    public String getHiddenField() {
        return hiddenField;
    }

    public void setHiddenField(String hiddenField) {
        this.hiddenField = hiddenField;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
