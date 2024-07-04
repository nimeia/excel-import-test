package org.example.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

public class ExcelDecimalFormat {
    public static void main(String[] args) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // 创建一个数字格式，保留两位小数
        CellStyle decimalStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        decimalStyle.setDataFormat(format.getFormat("#0.00"));
        sheet.setDefaultColumnStyle(0,decimalStyle);

        // 创建一个数值
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(new BigDecimal("1234.56789").doubleValue()); // 设置要格式化的数值

        // 应用格式
        //cell.setCellStyle(decimalStyle);


        // 调整列宽以显示完整内容
        sheet.autoSizeColumn(0);

        // 保存文件
        try (FileOutputStream fileOut = new FileOutputStream("./target/ExcelWithDecimalFormat.xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

