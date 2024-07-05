package org.example;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.*;
import org.example.business.CourseBusiness;
import org.example.business.TeacherBusiness;
import org.example.dbconfig.DbCellConfig;
import org.example.dbconfig.DbExcelConfig;
import org.example.dbconfig.DbSheetConfig;
import org.example.test.vo.MainVo;
import org.example.test.vo.Teacher;
import org.example.utils.XlsAnnotationUtils;
import org.example.utils.XlsGlobalUtils;
import org.example.vo.XlsCell;
import org.example.vo.XlsExcel;
import org.example.vo.XlsSheet;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbTestMain {

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String packageName = "org.example.db";
        List<DbExcelConfig> dbExcelConfigs = new ArrayList<>();
        DbExcelConfig dbExcelConfig = new DbExcelConfig();
        dbExcelConfigs.add(dbExcelConfig);
        dbExcelConfig.setCategory(new String[]{"type3|key3|name3", "type4|key4|name4", "type5|key6|name7"});
        dbExcelConfig.setTitle("db导入模板");
        dbExcelConfig.setPackageName(packageName);
        dbExcelConfig.setClassName("DbMainVo");

        List<DbSheetConfig> dbSheetConfigs = new ArrayList<>();
        dbExcelConfig.setDbSheetConfigs(dbSheetConfigs);
        DbSheetConfig dbTeacherSheetConfig = new DbSheetConfig();
        dbSheetConfigs.add(dbTeacherSheetConfig);
        dbTeacherSheetConfig.setHidden(false);
        dbTeacherSheetConfig.setIndex(1);
        dbTeacherSheetConfig.setTitle("教师");
        dbTeacherSheetConfig.setClassName("DbTeacher");
        dbTeacherSheetConfig.setToClass(TeacherBusiness.class.getName());

        List<DbCellConfig> dbTeacherCellConfigs = new ArrayList<DbCellConfig>();
        dbTeacherSheetConfig.setDbCellConfigs(dbTeacherCellConfigs);
        {
            DbCellConfig idCell = new DbCellConfig();
            idCell.setIndex(0);
            idCell.setFieldType(Integer.class.getName());
            idCell.setFieldName("id");
            idCell.setHeadTitle(new String[]{"ID"});
            idCell.setHeadStyle("redHead");
            dbTeacherCellConfigs.add(idCell);
        }
        {
            DbCellConfig idCard = new DbCellConfig();
            idCard.setIndex(1);
            idCard.setFieldType(String.class.getName());
            idCard.setFieldName("idCard");
            idCard.setDropdown(new String[]{"01-身份证", "02-其它"});
            idCard.setHeadTitle(new String[]{"证件类型"});
            dbTeacherCellConfigs.add(idCard);
        }
        {
            DbCellConfig email = new DbCellConfig();
            email.setIndex(2);
            email.setFieldType(String.class.getName());
            email.setFieldName("email");
            dbTeacherCellConfigs.add(email);
        }
        {
            DbCellConfig course = new DbCellConfig();
            course.setIndex(3);
            course.setHeadTitle(new String[]{"主键"});
            course.setToField("toId");
            course.setFieldType(List.class.getName());
            course.setFieldTypeClassName("Course");
            course.setFieldName("courses");
            course.setInnerSheetToClass(CourseBusiness.class.getName());
            course.setInnerSheetToField("id");
            course.setInnerSheetRowCount(3);
            course.setInnerSheetFieldType(Integer.class.getName());
            dbTeacherCellConfigs.add(course);
        }
        {
            DbCellConfig course = new DbCellConfig();
            course.setIndex(4);
            course.setFieldType(List.class.getName());
            course.setFieldTypeClassName("Course");
            course.setFieldName("courses");
            course.setInnerSheetToClass(CourseBusiness.class.getName());
            course.setInnerSheetToField("name");
            course.setInnerSheetRowCount(3);
            course.setInnerSheetFieldType(String.class.getName());
            dbTeacherCellConfigs.add(course);
        }

        //-------------------------------------
        // 创建一个 ClassPool 对象
        ClassPool pool = ClassPool.getDefault();

        //create all innerSheetClassFirst
        for (DbExcelConfig excelConfig : dbExcelConfigs) {
            for (DbSheetConfig dbSheetConfig : excelConfig.getDbSheetConfigs()) {
                for (DbCellConfig dbCellConfig : dbSheetConfig.getDbCellConfigs()) {
                    if (XlsAnnotationUtils.isNotEmptyStr(dbCellConfig.getFieldTypeClassName())) {
                        String classFullName = excelConfig.getPackageName() + "." + dbCellConfig.getFieldTypeClassName();
                        CtClass orNull = pool.getOrNull(classFullName);
                        if (orNull != null) continue; // 已经创建
                        //查出所有innerClass的属性
                        List<DbCellConfig> allInnerCellFields = dbSheetConfig.getDbCellConfigs().stream()
                                .filter(cell -> dbCellConfig.getFieldTypeClassName().equals(cell.getFieldTypeClassName()))
                                .toList();

                        CtClass innerSheetClass = pool.makeClass(classFullName);
                        ConstPool innserSheetClassConstPool = innerSheetClass.getClassFile().getConstPool();
                        //add field
                        int index = -1;
                        for (DbCellConfig innerCellField : allInnerCellFields) {
                            index ++;
                            // 创建一个新的字段
                            CtField ctInnerCellField = new CtField(pool.get(innerCellField.getInnerSheetFieldType()), innerCellField.getInnerSheetToField(), innerSheetClass);
                            ctInnerCellField.setModifiers(Modifier.PUBLIC);
                            //add XlsCell annotation
                            AnnotationsAttribute fieldAttr = new AnnotationsAttribute(innserSheetClassConstPool, AnnotationsAttribute.visibleTag);
                            Annotation xlsCellAnnotation = new Annotation(XlsCell.class.getName(), innserSheetClassConstPool);
                            xlsCellAnnotation.addMemberValue("index", new IntegerMemberValue(innserSheetClassConstPool, index/*innerCellField.getIndex()*/));
                            if (XlsAnnotationUtils.isNotEmptyStr(innerCellField.getToField())) {
                                xlsCellAnnotation.addMemberValue("toField", new StringMemberValue(innerCellField.getToField(), innserSheetClassConstPool));
                            }
                            if (innerCellField.getHeadTitle().length > 0) {
                                MemberValue[] titles = Arrays.stream(innerCellField.getHeadTitle())
                                        .map(f -> new StringMemberValue(f, innserSheetClassConstPool))
                                        .toList()
                                        .toArray(new MemberValue[]{});
                                ArrayMemberValue titlesArrayMemberValues = new ArrayMemberValue(innserSheetClassConstPool);
                                titlesArrayMemberValues.setValue(titles);
                                xlsCellAnnotation.addMemberValue("headTitle", titlesArrayMemberValues);
                            }
                            if (XlsAnnotationUtils.isNotEmptyStr(innerCellField.getHeadStyle())) {
                                xlsCellAnnotation.addMemberValue("headStyle", new StringMemberValue(innerCellField.getHeadStyle(), innserSheetClassConstPool));
                            }
                            if (XlsAnnotationUtils.isNotEmptyStr(innerCellField.getValidation())) {
                                xlsCellAnnotation.addMemberValue("validation", new StringMemberValue(innerCellField.getValidation(), innserSheetClassConstPool));
                            }
//                            if (!"void.class".equals(innerCellField.getInnerSheetToClass()) && XlsAnnotationUtils.isNotEmptyStr(innerCellField.getInnerSheetToClass())) {
//                                xlsCellAnnotation.addMemberValue("innerSheetToClass", new ClassMemberValue(innerCellField.getInnerSheetToClass(), innserSheetClassConstPool));
//                            }
//                            if (XlsAnnotationUtils.isNotEmptyStr(innerCellField.getInnerSheetToField())) {
//                                xlsCellAnnotation.addMemberValue("innerSheetToField", new StringMemberValue(innerCellField.getInnerSheetToField(), innserSheetClassConstPool));
//                            }
//                            if (innerCellField.getInnerSheetRowCount() > 0) {
//                                xlsCellAnnotation.addMemberValue("innerSheetRowCount", new IntegerMemberValue( innserSheetClassConstPool, innerCellField.getInnerSheetRowCount()));
//                            }
                            if (XlsAnnotationUtils.isNotEmptyStr(innerCellField.getDropSplit())) {
                                xlsCellAnnotation.addMemberValue("dropSplit", new StringMemberValue(innerCellField.getDropSplit(), innserSheetClassConstPool));
                            }
                            if (XlsAnnotationUtils.isNotEmptyStr(innerCellField.getDropdownSql())) {
                                xlsCellAnnotation.addMemberValue("dropdownSql", new StringMemberValue(innerCellField.getDropdownSql(), innserSheetClassConstPool));
                            }
                            if (innerCellField.getDropdown().length > 0) {
                                MemberValue[] dropdowns = Arrays.stream(innerCellField.getDropdown())
                                        .map(f -> new StringMemberValue(f, innserSheetClassConstPool))
                                        .toList()
                                        .toArray(new MemberValue[]{});
                                ArrayMemberValue titlesArrayMemberValues = new ArrayMemberValue(innserSheetClassConstPool);
                                titlesArrayMemberValues.setValue(dropdowns);
                                xlsCellAnnotation.addMemberValue("dropdown", titlesArrayMemberValues);
                            }
                            if (XlsAnnotationUtils.isNotEmptyStr(innerCellField.getFormat())) {
                                xlsCellAnnotation.addMemberValue("format", new StringMemberValue(innerCellField.getFormat(), innserSheetClassConstPool));
                            }
                            if (innerCellField.getColumnWeight() > 0) {
                                xlsCellAnnotation.addMemberValue("columnWeight", new IntegerMemberValue( innserSheetClassConstPool, innerCellField.getColumnWeight()));
                            }

                            fieldAttr.addAnnotation(xlsCellAnnotation);
                            // 将注解属性添加到字段
                            ctInnerCellField.getFieldInfo().addAttribute(fieldAttr);

                            innerSheetClass.addField(ctInnerCellField);
                        }

                        innerSheetClass.writeFile("./target/classes/");
                    }
                }
            }
        }
        for (DbExcelConfig excelConfig : dbExcelConfigs) {
            for (DbSheetConfig dbSheetConfig : excelConfig.getDbSheetConfigs()) {
                String classFullName = excelConfig.getPackageName() + "." + dbSheetConfig.getClassName();
                CtClass orNull = pool.getOrNull(classFullName);
                if (orNull != null) continue; // 已经创建
                CtClass sheetClass = pool.makeClass(classFullName);
                sheetClass.setModifiers(Modifier.PUBLIC);
                ClassFile sheetClassFile = sheetClass.getClassFile();
                ConstPool sheetClassConstPool = sheetClassFile.getConstPool();

                //add XlsSheet annotation
                AnnotationsAttribute classAnnotationAttr = new AnnotationsAttribute(sheetClassConstPool, AnnotationsAttribute.visibleTag);
                Annotation xlsSheetAnnotation = new Annotation(XlsSheet.class.getName(), sheetClassConstPool);
                if(dbSheetConfig.getIndex()!=null){
                    xlsSheetAnnotation.addMemberValue("index", new IntegerMemberValue(sheetClassConstPool, dbSheetConfig.getIndex()));
                }
                xlsSheetAnnotation.addMemberValue("title", new StringMemberValue(dbSheetConfig.getTitle(), sheetClassConstPool));
                if (dbSheetConfig.getSheetActive() != null) {
                    xlsSheetAnnotation.addMemberValue("sheetActive", new BooleanMemberValue(dbSheetConfig.getSheetActive(), sheetClassConstPool));
                }
                if (dbSheetConfig.getHeadRow() != null) {
                    xlsSheetAnnotation.addMemberValue("headRow", new IntegerMemberValue(sheetClassConstPool, dbSheetConfig.getHeadRow()));
                }
                if (dbSheetConfig.getHidden() != null) {
                    xlsSheetAnnotation.addMemberValue("hidden", new BooleanMemberValue(dbSheetConfig.getHidden(), sheetClassConstPool));
                }
                if (!"void.class".equals(dbSheetConfig.getToClass()) && XlsAnnotationUtils.isNotEmptyStr(dbSheetConfig.getToClass())) {
                    xlsSheetAnnotation.addMemberValue("toClass", new ClassMemberValue(dbSheetConfig.getToClass(), sheetClassConstPool));
                }
                if (!"void.class".equals(dbSheetConfig.getParentClass()) && XlsAnnotationUtils.isNotEmptyStr(dbSheetConfig.getParentClass())) {
                    xlsSheetAnnotation.addMemberValue("parentClass", new ClassMemberValue(dbSheetConfig.getParentClass(), sheetClassConstPool));
                }
                if (XlsAnnotationUtils.isNotEmptyStr(dbSheetConfig.getParentContainerField())) {
                    xlsSheetAnnotation.addMemberValue("parentContainerField", new StringMemberValue(dbSheetConfig.getParentContainerField(), sheetClassConstPool));
                }
                if (XlsAnnotationUtils.isNotEmptyStr(dbSheetConfig.getParentLinkId())) {
                    xlsSheetAnnotation.addMemberValue("parentLinkId", new StringMemberValue(dbSheetConfig.getParentLinkId(), sheetClassConstPool));
                }
                if (XlsAnnotationUtils.isNotEmptyStr(dbSheetConfig.getLinkId())) {
                    xlsSheetAnnotation.addMemberValue("linkId", new StringMemberValue(dbSheetConfig.getLinkId(), sheetClassConstPool));
                }
                classAnnotationAttr.addAnnotation(xlsSheetAnnotation);

                // 将注解属性添加到字段
                sheetClassFile.addAttribute(classAnnotationAttr);

                //add field
                for (DbCellConfig cellField : dbSheetConfig.getDbCellConfigs()) {
                    // 创建一个新的字段
                    if (Arrays.stream(sheetClass.getFields()).anyMatch(e ->e.getName().equals(cellField.getFieldName()))) {
                        continue;
                    }
                    CtField ctCellField = new CtField(pool.get(cellField.getFieldType()), cellField.getFieldName(), sheetClass);
                    ctCellField.setModifiers(Modifier.PUBLIC);
                    //add XlsCell annotation
                    AnnotationsAttribute fieldAttr = new AnnotationsAttribute(sheetClassConstPool, AnnotationsAttribute.visibleTag);
                    Annotation xlsCellAnnotation = new Annotation(XlsCell.class.getName(), sheetClassConstPool);
                    xlsCellAnnotation.addMemberValue("index", new IntegerMemberValue(sheetClassConstPool, cellField.getIndex()));
                    if (XlsAnnotationUtils.isNotEmptyStr(cellField.getToField())) {
                        xlsCellAnnotation.addMemberValue("toField", new StringMemberValue(cellField.getToField(), sheetClassConstPool));
                    }
                    if (cellField.getHeadTitle().length > 0) {
                        MemberValue[] titles = Arrays.stream(cellField.getHeadTitle())
                                .map(f -> new StringMemberValue(f, sheetClassConstPool))
                                .toList()
                                .toArray(new MemberValue[]{});
                        ArrayMemberValue titlesArrayMemberValues = new ArrayMemberValue(sheetClassConstPool);
                        titlesArrayMemberValues.setValue(titles);
                        xlsCellAnnotation.addMemberValue("headTitle", titlesArrayMemberValues);
                    }
                    if (XlsAnnotationUtils.isNotEmptyStr(cellField.getHeadStyle())) {
                        xlsCellAnnotation.addMemberValue("headStyle", new StringMemberValue(cellField.getHeadStyle(), sheetClassConstPool));
                    }
                    if (XlsAnnotationUtils.isNotEmptyStr(cellField.getValidation())) {
                        xlsCellAnnotation.addMemberValue("validation", new StringMemberValue(cellField.getValidation(), sheetClassConstPool));
                    }
                    if (!"void.class".equals(cellField.getFieldTypeClassName()) && XlsAnnotationUtils.isNotEmptyStr(cellField.getFieldTypeClassName())) {
                        // 创建一个新的字段
                        String sig = new SignatureAttribute.ClassSignature(
                                /*new SignatureAttribute.TypeParameter[]{
                                        new SignatureAttribute.TypeParameter(ctCellField.getName(),null,
                                                new SignatureAttribute.ObjectType[]{
                                                        new SignatureAttribute.ClassType(
                                                                cellField.getFieldTypeClassName()
                                                        )
                                                })
                                }*/null,
                                new SignatureAttribute.ClassType(ctCellField.getType().getName(),
                                        new SignatureAttribute.TypeArgument[]{
                                                new SignatureAttribute.TypeArgument(
                                                        new SignatureAttribute.ClassType(excelConfig.getPackageName() + "." +cellField.getFieldTypeClassName())
                                                )
                                        })
                                ,
                                /*new SignatureAttribute.ClassType[]{
                                        new SignatureAttribute.ClassType(ctCellField.getType().getName(),
                                                new SignatureAttribute.TypeArgument[]{
                                                        new SignatureAttribute.TypeArgument(
                                                                new SignatureAttribute.ClassType( excelConfig.getPackageName() + "." +cellField.getFieldTypeClassName())
                                                        )
                                                })
                                }*/null)
                                .encode();
                        ctCellField.setGenericSignature(sig);
                        //cellField.setGenericSignature(sig);
                    }
                    if (!"void.class".equals(cellField.getInnerSheetToClass()) && XlsAnnotationUtils.isNotEmptyStr(cellField.getInnerSheetToClass())) {
                        xlsCellAnnotation.addMemberValue("innerSheetToClass", new ClassMemberValue(cellField.getInnerSheetToClass(), sheetClassConstPool));
                    }
                    /*if (XlsAnnotationUtils.isNotEmptyStr(cellField.getInnerSheetToField())) {
                        xlsCellAnnotation.addMemberValue("innerSheetToField", new StringMemberValue(cellField.getInnerSheetToField(), sheetClassConstPool));
                    }*/
                    if (cellField.getInnerSheetRowCount() > 0) {
                        xlsCellAnnotation.addMemberValue("innerSheetRowCount", new IntegerMemberValue( sheetClassConstPool,cellField.getInnerSheetRowCount()));
                    }
                    if (XlsAnnotationUtils.isNotEmptyStr(cellField.getDropSplit())) {
                        xlsCellAnnotation.addMemberValue("dropSplit", new StringMemberValue(cellField.getDropSplit(), sheetClassConstPool));
                    }
                    if (XlsAnnotationUtils.isNotEmptyStr(cellField.getDropdownSql())) {
                        xlsCellAnnotation.addMemberValue("dropdownSql", new StringMemberValue(cellField.getDropdownSql(), sheetClassConstPool));
                    }
                    if (cellField.getDropdown().length > 0) {
                        MemberValue[] dropdowns = Arrays.stream(cellField.getDropdown())
                                .map(f -> new StringMemberValue(f, sheetClassConstPool))
                                .toList()
                                .toArray(new MemberValue[]{});
                        ArrayMemberValue titlesArrayMemberValues = new ArrayMemberValue(sheetClassConstPool);
                        titlesArrayMemberValues.setValue(dropdowns);
                        xlsCellAnnotation.addMemberValue("dropdown", titlesArrayMemberValues);
                    }
                    if (XlsAnnotationUtils.isNotEmptyStr(cellField.getFormat())) {
                        xlsCellAnnotation.addMemberValue("format", new StringMemberValue(cellField.getFormat(), sheetClassConstPool));
                    }
                    if (cellField.getColumnWeight() > 0) {
                        xlsCellAnnotation.addMemberValue("columnWeight", new IntegerMemberValue( sheetClassConstPool, cellField.getColumnWeight()));
                    }

                    fieldAttr.addAnnotation(xlsCellAnnotation);

                    // 将注解属性添加到字段
                    ctCellField.getFieldInfo().addAttribute(fieldAttr);
                    sheetClass.addField(ctCellField);
                }

                sheetClass.writeFile("./target/classes/");
            }
        }


        for (DbExcelConfig loopExcelConfig : dbExcelConfigs) {
            CtClass mainContainerClass = pool.makeClass(loopExcelConfig.getPackageName() + "." + loopExcelConfig.getClassName());
            mainContainerClass.setModifiers(Modifier.PUBLIC);
            ConstPool mainContainerClassClassPool = mainContainerClass.getClassFile().getConstPool();
            //add XLSExcel annotation
            AnnotationsAttribute classAttr = new AnnotationsAttribute(mainContainerClassClassPool, AnnotationsAttribute.visibleTag);
            Annotation xlsExcel = new Annotation(XlsExcel.class.getName(), mainContainerClassClassPool);
            xlsExcel.addMemberValue("title", new StringMemberValue(loopExcelConfig.getTitle(), mainContainerClassClassPool));
            ArrayMemberValue categoryArray = new ArrayMemberValue(mainContainerClassClassPool);
            MemberValue[] array = Arrays.stream(loopExcelConfig.getCategory())
                    .map(p -> new StringMemberValue(p, mainContainerClassClassPool))
                    .toList()
                    .toArray(new MemberValue[]{});
            categoryArray.setValue(array);
            xlsExcel.addMemberValue("category", categoryArray);
            classAttr.addAnnotation(xlsExcel);
            mainContainerClass.getClassFile().addAttribute(classAttr);

            // add sheet field
            for (DbSheetConfig dbSheetConfig : loopExcelConfig.getDbSheetConfigs()) {
                // 创建一个新的字段
                CtClass listClass = pool.get("java.util.List");
                CtClass type = pool.get(loopExcelConfig.getPackageName() + "." + dbSheetConfig.getClassName());
                CtField ctField = new CtField(listClass,
                        Character.toLowerCase(dbSheetConfig.getClassName().charAt(0)) +dbSheetConfig.getClassName().substring(1),
                        mainContainerClass);
                String sig = new SignatureAttribute.ClassSignature(null,
                        new SignatureAttribute.ClassType(listClass.getName(),
                                new SignatureAttribute.TypeArgument[]{
                                        new SignatureAttribute.TypeArgument(
                                                new SignatureAttribute.ClassType(type.getName())
                                        )
                                }),
                        /*new SignatureAttribute.ClassType[]{
                                new SignatureAttribute.ClassType(listClass.getName(),
                                new SignatureAttribute.TypeArgument[]{
                                        new SignatureAttribute.TypeArgument(
                                                new SignatureAttribute.ClassType(type.getName())
                                        )
                                })
                        }*/null).encode();
                ctField.setGenericSignature(sig);
                ctField.setModifiers(Modifier.PUBLIC);
                mainContainerClass.addField(ctField);
            }
            mainContainerClass.writeFile("./target/classes/");
        }

        XlsGlobalUtils.init(new String[]{
                dbExcelConfig.getPackageName()
        });

        Class<?> aClass = Class.forName(dbExcelConfig.getPackageName() + "." + dbExcelConfig.getClassName());
        Object o = aClass.getDeclaredConstructor().newInstance();
        System.out.println(o);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XlsGlobalUtils.getXlsTemplate(aClass,outputStream);

        try(FileOutputStream fileOutputStream = new FileOutputStream("./target/tempate-"+aClass.getSimpleName()+".xlsx")){
            fileOutputStream.write(outputStream.toByteArray());
        }
    }
}
