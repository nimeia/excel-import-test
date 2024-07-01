package org.example.business;

import org.example.vo.XlsCell;

import java.math.BigDecimal;

public class BusinessInnerSheet {

    private Integer innerId;

    private String name;

    private BigDecimal innerPrice;

    public BigDecimal getInnerPrice() {
        return innerPrice;
    }

    public void setInnerPrice(BigDecimal innerPrice) {
        this.innerPrice = innerPrice;
    }
}
