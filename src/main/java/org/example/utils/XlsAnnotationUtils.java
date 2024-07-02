package org.example.utils;

import org.example.vo.XlsCell;
import org.example.vo.XlsExcel;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class XlsAnnotationUtils {

    private static Map<String, Field> fieldCache = new HashMap<>();

    public static <T extends Annotation> List<T> getAllInitAnnotations(String[] basePackages, Class<T> tClass) {
        List<T> result = new ArrayList<>();
        for (Class<?> annotatedClass : getAllClassWithAnnotation(basePackages, tClass)) {
            result.add(annotatedClass.getAnnotation(tClass));
        }
        return result;
    }

    /**
     * get all class with the target annotation
     * @param basePackages  package to scan
     * @param tClass the annotation class
     * @return
     * @param <T>
     */
    public static <T extends Annotation> List<Class<?>> getAllClassWithAnnotation(String[] basePackages, Class<T> tClass) {
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
            getField(object.getClass(), fieldName).set(object, newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public static String buildSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public static String buildGetterName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    /**
     * @param clazz
     * @param propertyName
     * @return
     */
    public static Method getSetterMethod(Class<?> clazz, String propertyName) {
        if(propertyName==null || "".equals(propertyName)) throw new RuntimeException("propertyName is null!");
        String methodName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(e -> e.getName().equals(methodName))
                .findFirst()
                .orElse(null);
    }

    public static Method getGetterMethod(Class<?> clazz, String propertyName) {
        if(propertyName==null || "".equals(propertyName)) throw new RuntimeException("propertyName is null!");
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

    /**
     * 初始化 filed
     *
     * @param targetObj
     * @param field
     * @param i
     * @return
     */
    public static Object initField(Object targetObj, Field field, Method setMethod, Method getMethod, int i) {
        try {
            Object targetFiledObj = null;
            boolean newFlag = false;
            if (getMethod != null) {
                targetFiledObj = getMethod.invoke(targetObj);
            } else {
                targetFiledObj = field.get(targetObj);
            }
            if (targetFiledObj == null) {
                targetFiledObj = field.getType().getDeclaredConstructor().newInstance();
                newFlag = true;
                if (setMethod == null) {
                    setMethod.invoke(targetObj, targetFiledObj);
                } else {
                    field.set(targetObj, targetFiledObj);
                }
            }

            if (newFlag && Collection.class.isAssignableFrom(field.getType())) {
                // 当前集合的泛型类型
                Type genericType = field.getGenericType();
                ParameterizedType pt = (ParameterizedType) genericType;
                // 得到泛型里的class类型对象
                Class<?> collectionClassType = (Class<?>) pt.getActualTypeArguments()[0];
                Object collectionObj = collectionClassType.getDeclaredConstructor().newInstance();
                ((Collection)targetFiledObj).add(collectionObj);
            }

            return targetFiledObj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置 filed value
     * @param targetObj
     * @param fieldValue
     * @param field
     * @param setMethod
     * @param innerSheetField
     * @param innerSetMethod
     * @param index
     */
    public static void setFileValue(Object targetObj,
                                    Object fieldValue,
                                    Field field,
                                    Method setMethod,
                                    Field innerSheetField,
                                    Method innerSetMethod,
                                    Integer index) {
        try {
            if (innerSetMethod != null || innerSheetField != null) {
                Object realObj = targetObj;
                if (index != null && targetObj instanceof Collection<?>) {
                    while (((Collection<?>) targetObj).size() <= index) {
                        Type genericType = field.getGenericType();
                        ParameterizedType pt = (ParameterizedType) genericType;
                        // 得到泛型里的class类型对象
                        Class<?> collectionClassType = (Class<?>) pt.getActualTypeArguments()[0];
                        Object collectionObj = collectionClassType.getDeclaredConstructor().newInstance();
                        ((Collection) targetObj).add(collectionObj);
                    }

                    if (targetObj instanceof Set<?>) {
                        realObj = ((Set) targetObj).toArray()[index];
                    } else if (targetObj instanceof List<?>) {
                        realObj = ((List) targetObj).get(index);
                    }
                    if (innerSetMethod != null) {
                        innerSetMethod.invoke(realObj, fieldValue);
                    } else if (innerSheetField != null) {
                        innerSheetField.set(realObj, fieldValue);
                    }
                } else {
                    if (innerSetMethod != null) {
                        innerSetMethod.invoke(realObj, fieldValue);
                    } else if (innerSheetField != null) {
                        innerSheetField.set(realObj, fieldValue);
                    }
                }
            } else {
                Object realObj = targetObj;
                if (index != null) {
                    while (((Collection) targetObj).size() <= index) {
                        Type genericType = field.getGenericType();
                        ParameterizedType pt = (ParameterizedType) genericType;
                        // 得到泛型里的class类型对象
                        Class<?> collectionClassType = (Class<?>) pt.getActualTypeArguments()[0];
                        Object collectionObj = collectionClassType.getDeclaredConstructor().newInstance();
                        ((Collection) targetObj).add(collectionObj);
                    }

                    if (targetObj instanceof Set<?>) {
                        realObj = ((Set) targetObj).toArray()[index];
                    } else if (targetObj instanceof List<?>) {
                        realObj = ((List) targetObj).get(index);
                    }
                    if (setMethod != null) {
                        setMethod.invoke(realObj, fieldValue);
                    } else if (field != null) {
                        field.set(realObj, fieldValue);
                    }
                } else {
                    if (setMethod != null) {
                        setMethod.invoke(realObj, fieldValue);
                    } else if (field != null) {
                        field.set(realObj, fieldValue);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public static <T> T fieldValueForJdk12(Object o, String fieldName, Class<T> tClass) {
        if (o == null) return null;
        try {
            return (T) getField(o.getClass(), fieldName).get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class fieldCollectionRealType(Field field){
        if(Collection.class.isAssignableFrom(field.getType())){
            // 当前集合的泛型类型
            Type genericType = field.getGenericType();
            ParameterizedType pt = (ParameterizedType) genericType;
            // 得到泛型里的class类型对象
            return (Class<?>) pt.getActualTypeArguments()[0];
        }
        return null;
    }



    private static Field getField(Class clazz, String fieldName) {
        String key = clazz.getName() + "." + fieldName;
        return fieldCache.computeIfAbsent(key, k -> {
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

    public static boolean fieldContainsXlsCell(Class clazz) {
        return ReflectionUtils.getAllFields(clazz).stream().anyMatch(f->f.isAnnotationPresent(XlsCell.class));
    }

    public static Field getFieldByName(Class<?> clazz, String field) {
        Field field1 = ReflectionUtils.getAllFields(clazz).stream().filter(f -> f.getName().equals(field)).findFirst().orElse(null);
        if(field1!=null){
            field1.setAccessible(true);
        }
        return field1;
    }

    public static boolean isNotEmptyStr(String toFieldName) {
        return toFieldName!=null && !"".equals(toFieldName.trim());
    }

    public static Object getFieldValue(Object targetObj, Field field, Method getMethod,Field innerField ,Method innerGetMethod, Integer innerIndex) {
        try{
            Object result = null;
            if(getMethod!=null){
                result = getMethod.invoke(targetObj);
            }else if(field!=null){
                result = field.get(targetObj);
            }
            if(innerIndex!=null && innerIndex >=0 && result instanceof List<?>){
                result = ((List<?>) result).get(innerIndex);
            }

            if(innerGetMethod!=null){
                result = innerGetMethod.invoke(result);
            }else if(innerField!=null){
                result = innerGetMethod.invoke(result);
            }
            return result;
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }
}
