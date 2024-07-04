package org.example.test;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;
import java.util.List;

public class DynamicClassCreator {

    public static void main(String[] args) throws Exception {
        ClassPool pool = ClassPool.getDefault();

        // 创建一个新的类
        CtClass cc = pool.makeClass("com.example.DynamicClass");

        // 添加泛型 List 类型字段
        CtClass listClass = pool.get("java.util.List");
        CtField field = new CtField(listClass, "items", cc);
        field.setGenericSignature("Ljava/util/List<Ljava/lang/String;>;");
        cc.addField(field);

        // 添加注解
        ClassFile classFile = cc.getClassFile();
        ConstPool constPool = classFile.getConstPool();

        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation annot = new Annotation("javax.persistence.Entity", constPool);
        attr.addAnnotation(annot);
        classFile.addAttribute(attr);

        // 为类添加一个方法
//        CtMethod method = CtNewMethod.make(
//                "public void setItems(List<String> items) { this.items = items; }",
//                cc
//        );
//        cc.addMethod(method);

        // 将类写入磁盘
        cc.toClass().getCanonicalName();
        cc.writeFile("./target/dynamic-class-with-annotation");

        // 加载类并实例化对象
        Class<?> dynamicClass = cc.toClass();
        Object instance = dynamicClass.newInstance();

        System.out.println("Class created: " + dynamicClass.getName());
        System.out.println("Instance: " + instance);
    }
}
