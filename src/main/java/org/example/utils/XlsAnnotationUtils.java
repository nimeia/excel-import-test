package org.example.utils;

import org.example.vo.XlsExcel;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class XlsAnnotationUtils {

    private static Map<String, Field> fieldCache = new HashMap<>();

    public static <T extends Annotation> List<T> getAllInitAnnotations(String[] basePackages, Class<T> tClass) {
        List<T> result = new ArrayList<>();
        for (Class<?> annotatedClass : getAllClassWithAnnotiaon(basePackages, tClass)) {
            result.add(annotatedClass.getAnnotation(tClass));
        }
        return result;
    }

    public static <T extends Annotation> List<Class<?>> getAllClassWithAnnotiaon(String[] basePackages, Class<T> tClass) {
        List<XlsExcel> list = new ArrayList<XlsExcel>();
        //get all class with XlsExcel annotation
        // 创建 Reflections 对象，指定要扫描的包
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(basePackages) // 这里指定你的包路径
                .addScanners(new TypeAnnotationsScanner()));

        // 查找所有带有 MyAnnotation 注解的类
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(tClass);

        //
        return annotatedClasses.stream().toList();
    }

    /**
     * 修改注解值的方法
     */
    public static void setAnnotationValue1(Annotation annotation, String key, Object newValue) {
        try {
            InvocationHandler handler = Proxy.getInvocationHandler(annotation);
            Field memberValuesField = handler.getClass().getDeclaredField("memberValues");
            memberValuesField.setAccessible(true);
            Map<String, Object> memberValues = (Map<String, Object>) memberValuesField.get(handler);
            memberValues.put(key, newValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 设置值
     *
     * @param object
     * @param fieldName
     * @param newValue
     */
    public static void setFieldValue(Object object, String fieldName, Object newValue) {
        if (object == null) return;
        try {
            getField(object.getClass(),fieldName).set(object,newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param clazz
     * @param propertyName
     * @return
     */
    public static Method getSetterMethod(Class<?> clazz, String propertyName) {
        String methodName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(e -> e.getName().equals(methodName))
                .findFirst()
                .orElse(null);
    }

    public static Method getGetterMethod(Class<?> clazz, String propertyName) {
        String methodName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(e -> e.getName().equals(methodName))
                .findFirst()
                .orElse(null);
    }

    public static <T> T getFieldValue(Object o, String fieldName, Class<T> tClass) {
        try {
            Field secretField = Arrays.stream(o.getClass().getDeclaredFields())
                    .filter(e -> e.getName().equals(fieldName))
                    .findAny().orElse(null);
            // 设置属性可访问
            secretField.setAccessible(true);
            // 获取属性值 (因为是静态属性，所以传入 null)
            T secretValue = (T) secretField.get(o);
            return secretValue;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> T getFieldValueForJdk12(Object o, String fieldName, Class<T> tClass) {
        if(o == null) return null;
        try {
            return (T) getField(o.getClass(),fieldName).get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Field getField(Class clazz , String fieldName) {
        String key = clazz.getName() + "." + fieldName;
        return fieldCache.computeIfAbsent(key,k -> {
            try {
                Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
                getDeclaredFields0.setAccessible(true);
                Field[] fields = null;
                fields = (Field[]) getDeclaredFields0.invoke(clazz, false);

                Field modifiersField = null;
                for (Field each : fields) {
                    if (fieldName.equals(each.getName())) {
                        modifiersField = each;
                        break;
                    }
                }
                modifiersField.setAccessible(true);
                return modifiersField;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
