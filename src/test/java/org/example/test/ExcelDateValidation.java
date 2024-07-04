package org.example.test;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class ExcelDateValidation {
    public static void main(String[] args) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

//        sheet.getColumnStyle(0)

        // Create a cell style with date format
        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd"));

        // Create rows and cells with date values
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Date (yyyy-mm-dd)");

        for (int i = 1; i <= 10; i++) {
            Row row = sheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue("2024-01-" + (i < 10 ? "0" + i : i)); // Example dates
            cell.setCellStyle(dateCellStyle);
        }

        // Create a cell range for the data validation (A2:A11)
        CellRangeAddressList addressList = new CellRangeAddressList(1, 10, 0, 0);

        // Create the Data Validation
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dateConstraint = validationHelper.createDateConstraint(
                DataValidationConstraint.OperatorType.BETWEEN,
                "2024-01-01", // Start date
                "2024-12-31", // End date
                "yyyy-mm-dd"
        );
        DataValidation dataValidation = validationHelper.createValidation(dateConstraint, addressList);
        dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        dataValidation.setShowErrorBox(true);
        dataValidation.createErrorBox("Invalid Date", "Please enter a date in the format yyyy-mm-dd.");

        // Add the data validation to the sheet
        sheet.addValidationData(dataValidation);

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream("./target/workbook_with_date_validation.xlsx")) {
            workbook.write(fileOut);
        }

        // Close the workbook
        workbook.close();
    }
}
