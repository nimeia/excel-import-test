package org.example.test.vo;

import org.example.vo.XlsExcel;
import org.example.vo.XlsIgnore;

import java.util.List;
import java.util.Map;

@XlsExcel(title = "导入模板", category = {"type|key|name", "type1|key1|name1", "type2|key2|name2"})
public class MainVo extends BaseVo {

    @XlsIgnore
    Sheet1 sheet1;

    List<Sheet2> sheet2List;

    Map<?, ?> map;

    public Sheet1 getSheet1() {
        return sheet1;
    }

    public void setSheet1(Sheet1 sheet1) {
        this.sheet1 = sheet1;
    }

    public List<Sheet2> getSheet2List() {
        return sheet2List;
    }

    public void setSheet2List(List<Sheet2> sheet2List) {
        this.sheet2List = sheet2List;
    }

    public Map<?, ?> getMap() {
        return map;
    }

    public void setMap(Map<?, ?> map) {
        this.map = map;
    }
}
