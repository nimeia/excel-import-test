package org.example.validation;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.example.vo.XlsValidation;
import org.example.xls.config.ColumnValidation;

@XlsValidation
public class IntegerFormatValidation implements ColumnValidation {
    @Override
    public String getName() {
        return "Integer";
    }

    @Override
    public void validation(Workbook workbook, Sheet sheet,int firstRow, int lastRow, int firstCol, int lastCol) {
        // Create a cell range for the data validation (A2:A11)
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);


        // Create the Data Validation
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint integerConstraint = validationHelper.createIntegerConstraint(DataValidationConstraint.OperatorType.BETWEEN, String.valueOf(Integer.MIN_VALUE), String.valueOf(Integer.MAX_VALUE));
        DataValidation dataValidation = validationHelper.createValidation(integerConstraint, addressList);
        dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        dataValidation.setShowErrorBox(true);
        dataValidation.createErrorBox("Invalid Input", "Please enter an integer between "+Integer.MIN_VALUE+" and "+Integer.MAX_VALUE);

        // Add the data validation to the sheet
        sheet.addValidationData(dataValidation);

    }
}
