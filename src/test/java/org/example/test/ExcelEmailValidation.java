package org.example.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelEmailValidation {
    public static void main(String[] args) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // Create a cell range for the data validation (entire column A)
        CellRangeAddressList addressList = new CellRangeAddressList(1, 10, 0, 0);

        // Create the Data Validation
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint emailConstraint = validationHelper.createCustomConstraint("ISNUMBER(SEARCH(\"@\", A1:A100))");
        DataValidation dataValidation = validationHelper.createValidation(emailConstraint, addressList);
        dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        dataValidation.setShowErrorBox(true);
        dataValidation.createErrorBox("Invalid Email", "Please enter a valid email address.");

        // Add the data validation to the sheet
        sheet.addValidationData(dataValidation);

        // Create headers
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Email Address");

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream("./target/workbook_with_email_validation.xlsx")) {
            workbook.write(fileOut);
        }

        // Close the workbook
        workbook.close();
    }
}

