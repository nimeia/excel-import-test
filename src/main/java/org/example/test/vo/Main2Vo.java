package org.example.test.vo;

import org.example.vo.XlsExcel;

@XlsExcel(title = "导入模板", category = {"type|key1|name", "type1|key1|name1", "type2|key2|name2"}, bindClass = MainVo.class)
public class Main2Vo {


    Sheet1 sheet1;

    public Sheet1 getSheet1() {
        return sheet1;
    }

    public void setSheet1(Sheet1 sheet1) {
        this.sheet1 = sheet1;
    }
}

