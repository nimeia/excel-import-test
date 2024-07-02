package org.example.utils;

import org.apache.poi.ss.usermodel.*;

public class ExcelUtil {

    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return evaluateFormulaCell(cell);
            case BLANK:
                return "";
            case ERROR:
                return FormulaError.forInt(cell.getErrorCellValue()).getString();
            default:
                return null;
        }
    }

    private static Object evaluateFormulaCell(Cell cell) {
        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        CellValue cellValue = evaluator.evaluate(cell);

        switch (cellValue.getCellType()) {
            case STRING:
                return cellValue.getStringValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cellValue.getNumberValue();
                }
            case BOOLEAN:
                return cellValue.getBooleanValue();
            case ERROR:
                return FormulaError.forInt(cellValue.getErrorValue()).getString();
            default:
                return null;
        }
    }


    /**
     * 将Excel列序号转换为列标签
     * @param columnNumber Excel列序号（例如，1、2、27、703）
     * @return 对应的列标签（例如，A、B、AA、AAA）
     */
    public static String getColumnLabel(int columnNumber) {
        StringBuilder sb = new StringBuilder();
        while (columnNumber > 0) {
            int remainder = (columnNumber - 1) % 26;  // 计算余数，A对应0，Z对应25
            char ch = (char) ('A' + remainder);       // 转换为字母
            sb.insert(0, ch);                         // 插入到字符串的开头
            columnNumber = (columnNumber - 1) / 26;   // 减1除以26，获取下一个字母的值
        }
        return sb.toString();
    }
}
