package org.example.test.vo;

import org.example.vo.XlsIgnore;

public class BaseVo {

    @XlsIgnore
    SheetBase sheetBase;

    public SheetBase getSheetBase() {
        return sheetBase;
    }

    public void setSheetBase(SheetBase sheetBase) {
        this.sheetBase = sheetBase;
    }
}
