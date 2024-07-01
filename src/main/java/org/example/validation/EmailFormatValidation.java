package org.example.validation;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.example.vo.XlsValidation;
import org.example.xls.config.ColumnValidation;

@XlsValidation
public class EmailFormatValidation implements ColumnValidation {
    @Override
    public String getName() {
        return "email";
    }

    @Override
    public void validation(Workbook workbook, Sheet sheet,int firstRow, int lastRow, int firstCol, int lastCol) {
        // Create a cell range for the data validation (A2:A11)
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);

        // Create the Data Validation
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint emailConstraint = validationHelper.createCustomConstraint("ISNUMBER(SEARCH(\"@\", A2))");
        DataValidation dataValidation = validationHelper.createValidation(emailConstraint, addressList);
        dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        dataValidation.setShowErrorBox(true);
        dataValidation.createErrorBox("Invalid Email", "Please enter a valid email address.");

        // Add the data validation to the sheet
        sheet.addValidationData(dataValidation);
    }
}
