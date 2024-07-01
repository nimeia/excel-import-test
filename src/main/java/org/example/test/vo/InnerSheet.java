package org.example.test.vo;

import org.example.vo.XlsCell;

import java.math.BigDecimal;


public class InnerSheet {

    @XlsCell(index = 1, headTitle = {"inner组1", "子组2", "三级组1"})
    private Integer innerId;

    @XlsCell(index = 2, headTitle = {"inner组1", "子组2", "三级组2"})
    private String innerName;

    @XlsCell(index = 3, headTitle = {"inner组1", "子组1", "三级组3"})
    private BigDecimal innerPrice;

    public Integer getInnerId() {
        return innerId;
    }

    public void setInnerId(Integer innerId) {
        this.innerId = innerId;
    }

    public String getInnerName() {
        return innerName;
    }

    public void setInnerName(String innerName) {
        this.innerName = innerName;
    }

    public BigDecimal getInnerPrice() {
        return innerPrice;
    }

    public void setInnerPrice(BigDecimal innerPrice) {
        this.innerPrice = innerPrice;
    }
}
