package org.example.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;

public class ExcelEmailValidation {
    public static void main(String[] args) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // 数据验证助手
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();

        // 创建自定义公式进行电子邮件验证
        String emailRegex = "^[\\w\\-]+(\\.[\\w\\-]+)*@[\\w\\-]+(\\.[\\w\\-]+)*(\\.[a-zA-Z]{2,})$";
        //String customFormula = "AND(ISNUMBER(FIND(\"@\";CELL(\"contents\"));ISNUMBER(FIND(\".\";CELL(\"contents\")));NOT(ISNUMBER(FIND(\" \";CELL(\"contents\"))))";
        String customFormula = "AND(ISNUMBER(FIND(\"@\",CELL(\"address\"))),ISNUMBER(FIND(\".\",CELL(\"address\"))),NOT(ISNUMBER(FIND(\" \",CELL(\"address\"))))";

        System.out.println(customFormula);
        // 定义应用数据验证的单元格范围
        CellRangeAddressList addressList = new CellRangeAddressList(1, 100, 0, 0);

        // 创建数据验证约束
        DataValidationConstraint constraint = validationHelper.createCustomConstraint(customFormula);

        // 创建数据验证对象
        DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);

        // 启用输入框的错误提示
        dataValidation.setShowErrorBox(true);
        dataValidation.createErrorBox("Invalid Input", "Please input email.");


        // 添加数据验证到工作表
        sheet.addValidationData(dataValidation);

        // 保存工作簿
        try (FileOutputStream fileOut = new FileOutputStream("./target/ExcelWithEmailValidation.xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

