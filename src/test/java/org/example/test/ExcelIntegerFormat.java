package org.example.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelIntegerFormat {
    public static void main(String[] args) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // Create a cell style with integer format
        CellStyle integerCellStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        integerCellStyle.setDataFormat(format.getFormat("0"));

        // Create rows and cells with integer values
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Integer");

        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(123);
        cell.setCellStyle(integerCellStyle);

        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue(456);
        cell.setCellStyle(integerCellStyle);

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream("./target/workbook.xlsx")) {
            workbook.write(fileOut);
        }

        // Close the workbook
        workbook.close();
    }
}
