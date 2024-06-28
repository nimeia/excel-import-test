package org.example.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelDropdownValidation {
    public static void main(String[] args) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // Create a cell range for the data validation (A2:A11)
        CellRangeAddressList addressList = new CellRangeAddressList(1, 10, 0, 0);

        // Create the Data Validation
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dropdownConstraint = validationHelper.createExplicitListConstraint(new String[] {"红", "蓝", "绿"});
        DataValidation dataValidation = validationHelper.createValidation(dropdownConstraint, addressList);
        dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        dataValidation.setShowErrorBox(true);
        dataValidation.createErrorBox("Invalid Input", "Please select a value from the dropdown list.");

        // Add the data validation to the sheet
        sheet.addValidationData(dataValidation);

        // Create rows and cells with headers
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Color");

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream("./target/workbook_with_dropdown_validation.xlsx")) {
            workbook.write(fileOut);
        }

        // Close the workbook
        workbook.close();
    }
}
