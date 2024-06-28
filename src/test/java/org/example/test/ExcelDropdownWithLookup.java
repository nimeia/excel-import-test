package org.example.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelDropdownWithLookup {
    public static void main(String[] args) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // Create a hidden sheet with lookup values
        Sheet hiddenSheet = workbook.createSheet("Lookup");
        workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheet), true);

        // Add lookup values to the hidden sheet
        String[][] lookupData = {{"红", "red"}, {"蓝", "blue"}, {"绿", "green"}};
        for (int i = 0; i < lookupData.length; i++) {
            Row row = hiddenSheet.createRow(i);
            row.createCell(0).setCellValue(lookupData[i][0]); // Display value
            row.createCell(1).setCellValue(lookupData[i][1]); // Actual value
        }

        // Create a cell range for the dropdown validation (A2:A11)
        CellRangeAddressList addressList = new CellRangeAddressList(1, 10, 0, 0);

        // Create the Data Validation for display values
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dropdownConstraint = validationHelper.createFormulaListConstraint("Lookup!$A$1:$A$3");
        DataValidation dataValidation = validationHelper.createValidation(dropdownConstraint, addressList);
        dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        dataValidation.setShowErrorBox(true);
        dataValidation.createErrorBox("Invalid Input", "Please select a value from the dropdown list.");

        // Add the data validation to the sheet
        sheet.addValidationData(dataValidation);

        // Add formula to get actual value based on display value
        for (int i = 1; i <= 10; i++) {
            Row row = sheet.createRow(i);
            Cell displayCell = row.createCell(0);
            Cell actualCell = row.createCell(1);
            actualCell.setCellFormula("VLOOKUP(A" + (i + 1) + ",Lookup!$A$1:$B$3,2,FALSE)");
        }

        // Create headers
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Display Value");
        headerRow.createCell(1).setCellValue("Actual Value");

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream("./target/workbook_with_dropdown_and_lookup.xlsx")) {
            workbook.write(fileOut);
        }

        // Close the workbook
        workbook.close();
    }
}

