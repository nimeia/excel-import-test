package org.example.business;

import org.example.vo.XlsCell;

import java.math.BigDecimal;

public class StudentBusiness {


    private Integer id;

    private String name;

    private BigDecimal price;

    private String email;

    private Integer parentId;
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

}
