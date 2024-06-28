package org.example.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelIntegerValidation {
    public static void main(String[] args) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // Create a cell style with integer format
        CellStyle integerCellStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        integerCellStyle.setDataFormat(format.getFormat("0"));

        // Create rows and cells with integer values
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Integer Only");

        for (int i = 1; i <= 10; i++) {
            Row row = sheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(i);
            cell.setCellStyle(integerCellStyle);
        }

        // Create a cell range for the data validation (A2:A11)
        CellRangeAddressList addressList = new CellRangeAddressList(1, 10, 0, 0);

        // Create the Data Validation
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint integerConstraint = validationHelper.createIntegerConstraint(DataValidationConstraint.OperatorType.BETWEEN, "1", "1000");
        DataValidation dataValidation = validationHelper.createValidation(integerConstraint, addressList);
        dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        dataValidation.setShowErrorBox(true);
        dataValidation.createErrorBox("Invalid Input", "Please enter an integer between 1 and 1000.");

        // Add the data validation to the sheet
        sheet.addValidationData(dataValidation);

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream("./target/workbook_with_validation.xlsx")) {
            workbook.write(fileOut);
        }

        // Close the workbook
        workbook.close();
    }
}
