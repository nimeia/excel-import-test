package org.example.style;

import org.apache.poi.ss.usermodel.*;
import org.example.vo.XlsStyleHead;
import org.example.xls.config.HeadStyle;

@XlsStyleHead
public class RedHeadStyle implements HeadStyle {
    @Override
    public String getName() {
        return "redHead";
    }

    @Override
    public CellStyle headStyle(Sheet dataSheet) {
        CellStyle cellStyle = dataSheet.getWorkbook().createCellStyle();
        Font font = dataSheet.getWorkbook().createFont();
        // 设置字体加粗
        font.setBold(true);
        font.setColor(Font.COLOR_RED);
        cellStyle.setFont(font);
        // 设置内容居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }
}
