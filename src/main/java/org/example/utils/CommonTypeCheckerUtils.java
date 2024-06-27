package org.example.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CommonTypeCheckerUtils {
    private static final Set<Class<?>> commonTypes = new HashSet<>();

    static {
        commonTypes.add(Boolean.class);
        commonTypes.add(Character.class);
        commonTypes.add(Byte.class);
        commonTypes.add(Short.class);
        commonTypes.add(Integer.class);
        commonTypes.add(Long.class);
        commonTypes.add(Float.class);
        commonTypes.add(Double.class);
        commonTypes.add(String.class);
        commonTypes.add(BigInteger.class);
        commonTypes.add(BigDecimal.class);
        commonTypes.add(Date.class);
        // 你可以根据需要添加更多常用类型
    }

    public static boolean isCommonType(Object obj) {
        if (obj == null) {
            return true;
        }
        return commonTypes.contains(obj instanceof Class ? obj : obj.getClass());
    }
//
//    public static void main(String[] args) {
//        System.out.println(isCommonType(123));            // true
//        System.out.println(isCommonType("hello"));        // true
//        System.out.println(isCommonType(new Date()));     // true
//        System.out.println(isCommonType(new Object()));   // false
//        System.out.println(isCommonType(new int[]{}));    // false
//    }
}
