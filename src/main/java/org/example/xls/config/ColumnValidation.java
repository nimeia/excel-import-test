package org.example.xls.config;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public interface ColumnValidation {


    /**
     * 名称
     * @return
     */
    public String getName();

    /**
     * 增加校验
     * @param workbook
     * @param sheet
     */
    public void validation(Workbook workbook, Sheet sheet,int firstRow, int lastRow, int firstCol, int lastCol);
}
