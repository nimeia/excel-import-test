package org.example.utils;

import java.math.BigDecimal;

public class GenericComparator {

    /**
     * 比较两个对象的内容，数字类型支持自动转换
     * @param obj1 第一个对象
     * @param obj2 第二个对象
     * @return 比较结果，如果相等返回0，如果obj1小于obj2返回-1，如果obj1大于obj2返回1
     * @throws IllegalArgumentException 如果无法比较
     */
    public static int compare(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return 0;
        }
        if (obj1 == null) {
            return -1;
        }
        if (obj2 == null) {
            return 1;
        }

        // 如果是数字类型，转换为BigDecimal进行比较
        if (obj1 instanceof Number && obj2 instanceof Number) {
            BigDecimal num1 = toBigDecimal((Number) obj1);
            BigDecimal num2 = toBigDecimal((Number) obj2);
            return num1.compareTo(num2);
        }

        // 使用equals进行一般对象的比较
        if (obj1.equals(obj2)) {
            return 0;
        }

        // 比较字符串形式的对象内容
        int stringCompare = obj1.toString().compareTo(obj2.toString());
        if (stringCompare != 0) {
            return stringCompare;
        }

        throw new IllegalArgumentException("无法比较的对象类型: " + obj1.getClass() + " 和 " + obj2.getClass());
    }

    /**
     * 将Number类型转换为BigDecimal
     * @param number 要转换的数字
     * @return 转换后的BigDecimal
     */
    private static BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        }
        if (number instanceof Integer || number instanceof Long) {
            return new BigDecimal(number.longValue());
        }
        if (number instanceof Float || number instanceof Double) {
            return new BigDecimal(number.doubleValue());
        }
        if (number instanceof Byte || number instanceof Short) {
            return new BigDecimal(number.intValue());
        }
        throw new IllegalArgumentException("无法转换的数字类型: " + number.getClass());
    }

    public static void main(String[] args) {
        // 示例测试
        System.out.println(compare(10, 10.0)); // 输出 0
        System.out.println(compare(10, 20));   // 输出 -1
        System.out.println(compare(20.0, 10)); // 输出 1
        System.out.println(compare(null, 10)); // 输出 -1
        System.out.println(compare(10, null)); // 输出 1
        System.out.println(compare(null, null)); // 输出 0
        System.out.println(compare("abc", "abc")); // 输出 0
        System.out.println(compare("abc", "def")); // 输出 -1
    }
}

