package org.example.business;

import org.example.vo.XlsCell;

import java.math.BigDecimal;

public class BusinessSheet2 {


    private Integer id;

    private String name;

    private BigDecimal price;

    private String email;

    private BusinessInnerSheet businessInnerSheet;

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

    public BusinessInnerSheet getBusinessInnerSheet() {
        return businessInnerSheet;
    }

    public void setBusinessInnerSheet(BusinessInnerSheet businessInnerSheet) {
        this.businessInnerSheet = businessInnerSheet;
    }
}
