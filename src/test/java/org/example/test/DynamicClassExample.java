package org.example.test;

import javassist.*;
import org.reflections.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class DynamicClassExample {
    public static void main(String[] args) throws CannotCompileException, IOException, NoSuchFieldException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        try {
            // 创建一个 ClassPool 对象
            ClassPool pool = ClassPool.getDefault();

            // 创建一个新的类
            CtClass ctClass = pool.makeClass("com.example.DynamicClass");

            // 添加一个字段
            CtField ctField = new CtField(CtClass.intType, "id", ctClass);
            ctField.setModifiers(Modifier.PUBLIC);

            ctClass.addField(ctField);

            // 添加一个方法
            CtMethod ctMethod = new CtMethod(CtClass.voidType, "printId", new CtClass[]{}, ctClass);
            ctMethod.setModifiers(Modifier.PUBLIC);
            ctMethod.setBody("{ System.out.println(\"ID: \" + id); }");
            ctClass.addMethod(ctMethod);

            // 将类写入文件
            ctClass.writeFile("./target/");

            // 加载类并创建实例
            Class<?> dynamicClass = ctClass.toClass();
            Object instance = dynamicClass.newInstance();

            // 设置字段值
//            Field id = dynamicClass.getField("id");
//            id.setAccessible(true);
//            id.setInt(instance, 123);
            Set<Field> allFields = ReflectionUtils.getAllFields(dynamicClass);

            // 调用方法
            dynamicClass.getMethod("printId").invoke(instance);

            Object o = dynamicClass.getDeclaredConstructor().newInstance();
            System.out.println(o);
        } catch (Exception e) {
            throw e;
        }
    }
}
