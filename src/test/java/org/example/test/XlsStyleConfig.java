package org.example.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

public class XlsStyleConfig {

    // 公式：输入的数值必须在1到100之间
    String numericValidationFormula = "AND(ISNUMBER(A2), A2>=1, A2<=100)";
    // 公式：输入的文本长度必须在5到10个字符之间
    String textLengthValidationFormula = "AND(LEN(A2)>=5, LEN(A2)<=10)";
    // 公式：输入的日期必须在2022-01-01到2022-12-31之间
    String dateValidationFormula = "AND(ISNUMBER(A2), A2>=DATE(2022,1,1), A2<=DATE(2022,12,31))";
    // 公式：输入的文本必须是有效的电子邮件格式
    String emailValidationFormula = "AND(ISNUMBER(FIND(\"@\",A2)),ISNUMBER(FIND(\".\",A2)),LEN(A2)-LEN(SUBSTITUTE(A2,\"@\",\"\"))=1,LEN(A2)-LEN(SUBSTITUTE(A2,\".\",\"\"))>=1)";
    // 公式：输入的文本必须是 "Yes" 或 "No"
    String specificTextValidationFormula = "OR(A2=\"Yes\", A2=\"No\")";
    // 公式：输入的值必须是整数
    String integerValidationFormula = "INT(A2)=A2";
    // 公式：输入的文本必须是10位数字
    String phoneNumberValidationFormula = "AND(ISNUMBER(A2), LEN(TEXT(A2, \"0\"))=10)";
    // 公式：单元格不能为空
    String notEmptyValidationFormula = "A2<>\"\"";


    public CellStyle headStyle(Sheet dataSheet) {
        CellStyle cellStyle = dataSheet.getWorkbook().createCellStyle();
        Font font = dataSheet.getWorkbook().createFont();
        // 设置字体加粗
        font.setBold(true);
        cellStyle.setFont(font);
        // 设置内容居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    public CellStyle dateStyle(Sheet dataSheet) {
        // 创建日期格式
        CreationHelper createHelper = dataSheet.getWorkbook().getCreationHelper();
        CellStyle cellStyle = dataSheet.getWorkbook().createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        return cellStyle;
    }


    public void emailValidation(Sheet dataSheet) {
        // 定义电子邮件验证公式
        String emailValidationFormula = "AND(ISNUMBER(FIND(\"@\",A2)),ISNUMBER(FIND(\".\",A2)),LEN(A2)-LEN(SUBSTITUTE(A2,\"@\",\"\"))=1,LEN(A2)-LEN(SUBSTITUTE(A2,\".\",\"\"))>=1)";

        // 创建数据验证对象
        DataValidationHelper validationHelper = dataSheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createCustomConstraint(emailValidationFormula);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1, 0, 0);
        DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);

        // 设置数据验证的输入提示和错误提示
        dataValidation.setShowErrorBox(true);
        dataValidation.createErrorBox("Invalid Email", "Please enter a valid email address.");
        dataValidation.setShowPromptBox(true);
        dataValidation.createPromptBox("Email Input", "Please enter your email address in this format: example@domain.com");

        // 将数据验证应用到工作表
        dataSheet.addValidationData(dataValidation);
    }

    @Override
    public String toString() {
        return "XlsStyleConfig{" +
                "numericValidationFormula='" + numericValidationFormula + '\'' +
                ", textLengthValidationFormula='" + textLengthValidationFormula + '\'' +
                ", dateValidationFormula='" + dateValidationFormula + '\'' +
                ", emailValidationFormula='" + emailValidationFormula + '\'' +
                ", specificTextValidationFormula='" + specificTextValidationFormula + '\'' +
                ", integerValidationFormula='" + integerValidationFormula + '\'' +
                ", phoneNumberValidationFormula='" + phoneNumberValidationFormula + '\'' +
                ", notEmptyValidationFormula='" + notEmptyValidationFormula + '\'' +
                '}';
    }
}
