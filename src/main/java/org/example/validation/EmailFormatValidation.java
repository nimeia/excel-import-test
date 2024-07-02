package org.example.validation;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.example.utils.ExcelUtil;
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

        // 数据验证助手
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();

        // 创建自定义公式进行电子邮件验证
        String label = ExcelUtil.getColumnLabel(firstCol)+firstRow;
        String customFormula = "AND(ISNUMBER(FIND(\"@\","+label+")),ISNUMBER(FIND(\".\","+label+")),NOT(ISNUMBER(FIND(\" \","+label+"))))";

        // 定义应用数据验证的单元格范围
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);

        // 创建数据验证约束
        DataValidationConstraint constraint = validationHelper.createCustomConstraint(customFormula);

        // 创建数据验证对象
        DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);

        // 启用输入框的错误提示
        dataValidation.setShowErrorBox(true);
        dataValidation.createErrorBox("Invalid Input", "Please input email.");


        // 添加数据验证到工作表
        sheet.addValidationData(dataValidation);
    }
}
