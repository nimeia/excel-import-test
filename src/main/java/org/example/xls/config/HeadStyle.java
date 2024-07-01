package org.example.xls.config;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;

public interface HeadStyle {

    /**
     * 名称
     * @return
     */
    public String getName();

    /**
     * 设置style 代码逻辑
     * @param dataSheet
     * @return
     */
    public CellStyle headStyle(Sheet dataSheet);
}
