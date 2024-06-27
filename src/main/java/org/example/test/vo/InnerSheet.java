package org.example.test.vo;

import org.example.vo.XlsCell;

import java.math.BigDecimal;


public class InnerSheet {

    @XlsCell(headTitle = {"inner组1", "子组2", "三级组1"})
    private Integer innerId;

    @XlsCell(headTitle = {"inner组1", "子组2", "三级组2"})
    private String innerName;

    @XlsCell(headTitle = {"inner组1", "子组1", "三级组2"})
    private BigDecimal innerPrice;
}
