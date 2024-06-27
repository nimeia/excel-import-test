package org.example.business;

import org.example.vo.XlsCell;

import java.math.BigDecimal;

public class BusinessSheet2 {


    @XlsCell(headTitle = {"组1", "子组2", "三级组1"}, styleMethod = "headStyle")
    private Integer id;

    @XlsCell(headTitle = {"组1", "子组2", "三级组2"}, styleMethod = "headStyle")
    private String name;

    @XlsCell(headTitle = {"组1", "子组2", "三级组2"}, styleMethod = "headStyle")
    private BigDecimal price;

    @XlsCell(index = 3, styleMethod = "headStyle")
    private String email;

}
