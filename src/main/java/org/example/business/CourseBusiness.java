package org.example.business;

import java.math.BigDecimal;

public class CourseBusiness {

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
