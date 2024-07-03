package org.example.test;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class DynamicClassWithAnnotation {
    public static void main(String[] args) throws CannotCompileException, IOException, NoSuchFieldException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        try {
            // 创建一个 ClassPool 对象
            ClassPool pool = ClassPool.getDefault();

            // 创建一个新的类
            CtClass ctClass = pool.makeClass("com.example.DynamicClass");

            // 创建一个新的字段
            CtField ctField = new CtField(CtClass.intType, "id", ctClass);
            ctField.setModifiers(Modifier.PUBLIC);

            // 获取字段的属性对象
            ConstPool constPool = ctClass.getClassFile().getConstPool();
            AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);

            // 创建注解 @javax.validation.constraints.NotNull
            Annotation notNullAnnotation = new Annotation("javax.validation.constraints.NotNull", constPool);
            fieldAttr.addAnnotation(notNullAnnotation);

            // 创建注解 @javax.validation.constraints.Size
            Annotation sizeAnnotation = new Annotation("javax.validation.constraints.Size", constPool);
            sizeAnnotation.addMemberValue("min", new StringMemberValue("1", constPool));
            sizeAnnotation.addMemberValue("max", new StringMemberValue("10", constPool));
            fieldAttr.addAnnotation(sizeAnnotation);

            // 将注解属性添加到字段
            ctField.getFieldInfo().addAttribute(fieldAttr);

            // 将字段添加到类中
            ctClass.addField(ctField);

            // 添加一个方法
            CtMethod ctMethod = new CtMethod(CtClass.voidType, "printId", new CtClass[]{}, ctClass);
            ctMethod.setModifiers(Modifier.PUBLIC);
            ctMethod.setBody("{ System.out.println(\"ID: \" + id); }");
            ctClass.addMethod(ctMethod);

            // 将类写入文件
            //ctClass.writeFile("./");

            // 加载类并创建实例
            Class<?> dynamicClass = ctClass.toClass();
            Object instance = dynamicClass.newInstance();

            // 设置字段值
            dynamicClass.getField("id").setInt(instance, 123);

            // 调用方法
            dynamicClass.getMethod("printId").invoke(instance);

            // 打印注解信息
            java.lang.annotation.Annotation[] annotations = dynamicClass.getField("id").getAnnotations();
            for (java.lang.annotation.Annotation annotation : annotations) {
                System.out.println(annotation);
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
