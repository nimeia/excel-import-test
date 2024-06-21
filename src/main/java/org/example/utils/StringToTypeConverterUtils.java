package org.example.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StringToTypeConverterUtils {

    public static Object convert(String str, Class<?> clazz) throws Exception {
        if (clazz == String.class || clazz == CharSequence.class) {
            return str;
        }else if (clazz == Integer.class || clazz == int.class) {
            return toInt(str);
        } else if (clazz == Double.class || clazz == double.class) {
            return toDouble(str);
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            return toBoolean(str);
        } else if (clazz == Long.class || clazz == long.class) {
            return toLong(str);
        } else if (clazz == Float.class || clazz == float.class) {
            return toFloat(str);
        } else if (clazz == Short.class || clazz == short.class) {
            return toShort(str);
        } else if (clazz == Byte.class || clazz == byte.class) {
            return toByte(str);
        } else if (clazz == Character.class || clazz == char.class) {
            return toChar(str);
        } else if (clazz == BigDecimal.class) {
            return toBigDecimal(str);
        } else if (clazz == Date.class) {
            // 默认日期格式，如果需要其他格式，可以额外传递参数或重载方法
            return parseDate(str);
        } else if (clazz == List.class) {
            return toList(str);
        } else if (clazz.isArray()) {
            Class<?> componentType = clazz.getComponentType();
            if (componentType == int.class) {
                return toIntArray(str);
            } else if (componentType == double.class) {
                return toDoubleArray(str);
            } else if (componentType == boolean.class) {
                return toBooleanArray(str);
            }
            // 可以扩展更多类型数组的处理
        }
        throw new IllegalArgumentException("Unsupported class type: " + clazz.getName());
    }

    public static int toInt(String str) throws NumberFormatException {
        if (str == null || str.trim().isEmpty()) {
            throw new NumberFormatException("Null or empty string");
        }
        return Double.valueOf(str).intValue();
    }

    public static double toDouble(String str) throws NumberFormatException {
        if (str == null || str.trim().isEmpty()) {
            throw new NumberFormatException("Null or empty string");
        }
        return Double.parseDouble(str.trim());
    }

    public static boolean toBoolean(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        return Boolean.parseBoolean(str.trim());
    }

    public static long toLong(String str) throws NumberFormatException {
        if (str == null || str.trim().isEmpty()) {
            throw new NumberFormatException("Null or empty string");
        }
        return Long.parseLong(str.trim());
    }

    public static float toFloat(String str) throws NumberFormatException {
        if (str == null || str.trim().isEmpty()) {
            throw new NumberFormatException("Null or empty string");
        }
        return Float.parseFloat(str.trim());
    }

    public static short toShort(String str) throws NumberFormatException {
        if (str == null || str.trim().isEmpty()) {
            throw new NumberFormatException("Null or empty string");
        }
        return Short.parseShort(str.trim());
    }

    public static byte toByte(String str) throws NumberFormatException {
        if (str == null || str.trim().isEmpty()) {
            throw new NumberFormatException("Null or empty string");
        }
        return Byte.parseByte(str.trim());
    }

    public static char toChar(String str) throws IllegalArgumentException {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException("Null or empty string");
        }
        if (str.trim().length() != 1) {
            throw new IllegalArgumentException("String length is not 1");
        }
        return str.trim().charAt(0);
    }

    public static <T extends Enum<T>> T toEnum(String str, Class<T> enumType) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException("Null or empty string");
        }
        return Enum.valueOf(enumType, str.trim());
    }

    public static Date toDate(String str, String dateFormat) throws ParseException {
        if (str == null || str.trim().isEmpty()) {
            throw new ParseException("Null or empty string", 0);
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.parse(str.trim());
    }

    public static BigDecimal toBigDecimal(String str) throws NumberFormatException {
        if (str == null || str.trim().isEmpty()) {
            throw new NumberFormatException("Null or empty string");
        }
        return new BigDecimal(str.trim());
    }

    public static List<String> toList(String str) {
        if (str == null || str.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(str.trim().split("\\s*,\\s*"));
    }

    public static int[] toIntArray(String str) throws NumberFormatException {
        if (str == null || str.trim().isEmpty()) {
            return new int[0];
        }
        String[] parts = str.trim().split("\\s*,\\s*");
        int[] intArray = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            intArray[i] = Integer.parseInt(parts[i]);
        }
        return intArray;
    }

    public static double[] toDoubleArray(String str) throws NumberFormatException {
        if (str == null || str.trim().isEmpty()) {
            return new double[0];
        }
        String[] parts = str.trim().split("\\s*,\\s*");
        double[] doubleArray = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            doubleArray[i] = Double.parseDouble(parts[i]);
        }
        return doubleArray;
    }

    public static boolean[] toBooleanArray(String str) {
        if (str == null || str.trim().isEmpty()) {
            return new boolean[0];
        }
        String[] parts = str.trim().split("\\s*,\\s*");
        boolean[] booleanArray = new boolean[parts.length];
        for (int i = 0; i < parts.length; i++) {
            booleanArray[i] = Boolean.parseBoolean(parts[i]);
        }
        return booleanArray;
    }
    public static Date parseDate(String dateString) throws ParseException {
        // 常见的日期时间格式
        String[] formats = {
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd",
                "MM/dd/yyyy HH:mm:ss",
                "MM/dd/yyyy",
                "dd/MM/yyyy HH:mm:ss",
                "dd/MM/yyyy",
                "yyyy/MM/dd",
                "dd-MMM-yyyy",
                "HH:mm:ss",
                "hh:mm:ss a"
                // 添加其他可能的日期时间格式
        };

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.parse(dateString);
            } catch (ParseException e) {
                // 尝试下一个格式
            }
        }

        // 如果所有格式都尝试过了，仍然无法解析，则抛出 ParseException
        throw new ParseException("Unparseable date: " + dateString, 0);
    }

    public static void main(String[] args) {
        try {
            // 基本类型转换
            int intValue = StringToTypeConverterUtils.toInt("123");
            double doubleValue = StringToTypeConverterUtils.toDouble("123.45");
            boolean booleanValue = StringToTypeConverterUtils.toBoolean("true");
            long longValue = StringToTypeConverterUtils.toLong("123456789");
            float floatValue = StringToTypeConverterUtils.toFloat("123.45");
            short shortValue = StringToTypeConverterUtils.toShort("123");
            byte byteValue = StringToTypeConverterUtils.toByte("123");
            char charValue = StringToTypeConverterUtils.toChar("a");
            BigDecimal bigDecimalValue = StringToTypeConverterUtils.toBigDecimal("123.45");

            // 日期转换
            String dateStr = "2024-06-18";
            Date dateValue = StringToTypeConverterUtils.toDate(dateStr, "yyyy-MM-dd");

            // 列表和数组转换
            List<String> listValue = StringToTypeConverterUtils.toList("a, b, c");
            int[] intArray = StringToTypeConverterUtils.toIntArray("1, 2, 3");
            double[] doubleArray = StringToTypeConverterUtils.toDoubleArray("1.1, 2.2, 3.3");
            boolean[] booleanArray = StringToTypeConverterUtils.toBooleanArray("true, false, true");

            // 示例枚举类型
            enum Color {RED, GREEN, BLUE}
            Color color = StringToTypeConverterUtils.toEnum("RED", Color.class);

            // 打印转换结果
            System.out.println("int: " + intValue);
            System.out.println("double: " + doubleValue);
            System.out.println("boolean: " + booleanValue);
            System.out.println("long: " + longValue);
            System.out.println("float: " + floatValue);
            System.out.println("short: " + shortValue);
            System.out.println("byte: " + byteValue);
            System.out.println("char: " + charValue);
            System.out.println("BigDecimal: " + bigDecimalValue);
            System.out.println("Date: " + dateValue);
            System.out.println("Date: "+ convert(dateStr, Date.class));
            System.out.println("List: " + listValue);
            System.out.println("int array: " + Arrays.toString(intArray));
            System.out.println("double array: " + Arrays.toString(doubleArray));
            System.out.println("boolean array: " + Arrays.toString(booleanArray));
            System.out.println("enum: " + color);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
