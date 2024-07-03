package org.example.validation;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.example.vo.XlsValidation;
import org.example.xls.config.ColumnValidation;

@XlsValidation
public class DateFormatValidation implements ColumnValidation {
    @Override
    public String getName() {
        return "Date";
    }

    @Override
    public void validation(Workbook workbook, Sheet sheet,int firstRow, int lastRow, int firstCol, int lastCol) {
        // Create a cell range for the data validation (A2:A11)
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        // Create the Data Validation
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint integerConstraint =
                validationHelper.createDateConstraint(
                        DataValidationConstraint.OperatorType.GREATER_OR_EQUAL,
                        "1000/01/01",
                        "9999/12/31",
                        "YYYY/MM/DD");
        DataValidation dataValidation = validationHelper.createValidation(integerConstraint, addressList);
        dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        dataValidation.setShowErrorBox(true);
        dataValidation.createErrorBox("Invalid Input", "Please enter an Date between 1000-01-01 and 9999-12-31");

        // Add the data validation to the sheet
        sheet.addValidationData(dataValidation);

    }
}
