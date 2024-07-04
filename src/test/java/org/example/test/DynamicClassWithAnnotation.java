package org.example.test;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

public class DynamicClassWithAnnotation {

    public static void main(String[] args) {
        try {
            // 创建类池
            ClassPool pool = ClassPool.getDefault();

            // 创建一个新的类
            CtClass cc = pool.makeClass("com.example.DynamicClass");

            // 添加一个泛型 List<String> 类型的字段
            CtClass listClass = pool.get("java.util.List");
            CtField field = new CtField(listClass, "items", cc);
            field.setGenericSignature("Ljava/util/List<Ljava/lang/String;>;");
            cc.addField(field);

            // 添加注解到类上
            ClassFile classFile = cc.getClassFile();
            ConstPool constPool = classFile.getConstPool();
            AnnotationsAttribute classAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
            Annotation entityAnnot = new Annotation("javax.persistence.Entity", constPool);
            classAttr.addAnnotation(entityAnnot);
            classFile.addAttribute(classAttr);

            // 为类添加一个方法
//            CtMethod method = CtNewMethod.make(
//                    "public void setItems(java.util.List<String> items) { this.items = items; }",
//                    cc
//            );
//            cc.addMethod(method);

            // 添加注解到字段上
            AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
            Annotation fieldAnnot = new Annotation("javax.persistence.Column", constPool);
            fieldAttr.addAnnotation(fieldAnnot);
            field.getFieldInfo().addAttribute(fieldAttr);

            // 将类写入磁盘
            cc.writeFile("./target/dynamic-class-with-annotation");

            // 打印类的内容
            System.out.println(cc.toClass().getCanonicalName() + " class generated.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
