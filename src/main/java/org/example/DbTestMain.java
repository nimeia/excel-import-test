package org.example;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.*;
import org.example.dbconfig.DbCellConfig;
import org.example.dbconfig.DbExcelConfig;
import org.example.dbconfig.DbSheetConfig;
import org.example.utils.XlsAnnotationUtils;
import org.example.vo.XlsCell;
import org.example.vo.XlsExcel;
import org.example.vo.XlsSheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbTestMain {

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException {
        String packageName = "org.example.db";
        List<DbExcelConfig> dbExcelConfigs = new ArrayList<>();
        DbExcelConfig dbExcelConfig = new DbExcelConfig();
        dbExcelConfigs.add(dbExcelConfig);
        dbExcelConfig.setCategory(new String[]{"type|key|name", "type1|key1|name1", "type2|key2|name2"});
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
        dbTeacherSheetConfig.setToClass("DbTeacherBusiness");

        List<DbCellConfig> dbTeacherCellConfigs = new ArrayList<DbCellConfig>();
        dbTeacherSheetConfig.setDbCellConfigs(dbTeacherCellConfigs);
        {
            DbCellConfig idCell = new DbCellConfig();
            idCell.setIndex(0);
            idCell.setFieldName(Integer.class.getName());
            idCell.setFieldName("id");
            idCell.setIndex(0);
            idCell.setHeadTitle(new String[]{"ID"});
            idCell.setHeadStyle("redHead");
            dbTeacherCellConfigs.add(idCell);
        }
        {
            DbCellConfig idCard = new DbCellConfig();
            idCard.setIndex(1);
            idCard.setFieldName(String.class.getName());
            idCard.setFieldName("idCard");
            idCard.setIndex(1);
            idCard.setDropdown(new String[]{"01-身份证", "02-其它"});
            idCard.setHeadTitle(new String[]{"证件类型"});
            dbTeacherCellConfigs.add(idCard);
        }
        {
            DbCellConfig email = new DbCellConfig();
            email.setIndex(2);
            email.setFieldName(String.class.getName());
            email.setFieldName("email");
            email.setIndex(2);
            dbTeacherCellConfigs.add(email);
        }
        {
            DbCellConfig course = new DbCellConfig();
            course.setIndex(3);
            course.setFieldName(List.class.getName());
            course.setFieldName("courses");
            course.setInnerSheetRowCount(3);
            course.setIndex(3);
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
                        for (DbCellConfig innerCellField : allInnerCellFields) {
                            // 创建一个新的字段
                            CtField ctInnerCellField = new CtField(pool.get(innerCellField.getFieldType()), innerCellField.getFieldName(), innerSheetClass);
                            ctInnerCellField.setModifiers(Modifier.PUBLIC);
                            //add XlsCell annotation
                            AnnotationsAttribute fieldAttr = new AnnotationsAttribute(innserSheetClassConstPool, AnnotationsAttribute.visibleTag);
                            Annotation xlsCellAnnotation = new Annotation(XlsCell.class.getName(), innserSheetClassConstPool);
                            xlsCellAnnotation.addMemberValue("index", new IntegerMemberValue(innerCellField.getIndex(), innserSheetClassConstPool));
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
                            if (XlsAnnotationUtils.isNotEmptyStr(innerCellField.getInnerSheetToClass())) {
                                xlsCellAnnotation.addMemberValue("innerSheetToClass", new StringMemberValue(innerCellField.getInnerSheetToClass(), innserSheetClassConstPool));
                            }
                            if (XlsAnnotationUtils.isNotEmptyStr(innerCellField.getInnerSheetToField())) {
                                xlsCellAnnotation.addMemberValue("innerSheetToField", new StringMemberValue(innerCellField.getInnerSheetToField(), innserSheetClassConstPool));
                            }
                            if (innerCellField.getInnerSheetRowCount() > 0) {
                                xlsCellAnnotation.addMemberValue("innerSheetRowCount", new IntegerMemberValue(innerCellField.getInnerSheetRowCount(), innserSheetClassConstPool));
                            }
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
                                xlsCellAnnotation.addMemberValue("columnWeight", new IntegerMemberValue(innerCellField.getColumnWeight(), innserSheetClassConstPool));
                            }

                            fieldAttr.addAnnotation(xlsCellAnnotation);
                            // 将注解属性添加到字段
                            ctInnerCellField.getFieldInfo().addAttribute(fieldAttr);
                        }

                        innerSheetClass.writeFile("./target/dbconfig/");
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
                ClassFile shhetClassFile = sheetClass.getClassFile();
                ConstPool sheetClassConstPool = shhetClassFile.getConstPool();

                //add XlsSheet annotation
                AnnotationsAttribute classAnnotationAttr = new AnnotationsAttribute(sheetClassConstPool, AnnotationsAttribute.visibleTag);
                Annotation xlsSheetAnnotation = new Annotation(XlsSheet.class.getName(), sheetClassConstPool);
                if(dbSheetConfig.getIndex()!=null){
                    xlsSheetAnnotation.addMemberValue("index", new StringMemberValue(dbSheetConfig.getIndex(), sheetClassConstPool));
                }
                xlsSheetAnnotation.addMemberValue("title", new StringMemberValue(dbSheetConfig.getTitle(), sheetClassConstPool));
                if (dbSheetConfig.getSheetActive() != null) {
                    xlsSheetAnnotation.addMemberValue("sheetActive", new BooleanMemberValue(dbSheetConfig.getSheetActive(), sheetClassConstPool));
                }
                if (dbSheetConfig.getHeadRow() != null) {
                    xlsSheetAnnotation.addMemberValue("headRow", new IntegerMemberValue(dbSheetConfig.getHeadRow(), sheetClassConstPool));
                }
                if (dbSheetConfig.getHidden() != null) {
                    xlsSheetAnnotation.addMemberValue("hidden", new BooleanMemberValue(dbSheetConfig.getHidden(), sheetClassConstPool));
                }
                if (XlsAnnotationUtils.isNotEmptyStr(dbSheetConfig.getToClass())) {
                    xlsSheetAnnotation.addMemberValue("toClass", new ClassMemberValue(dbSheetConfig.getToClass(), sheetClassConstPool));
                }
                if (XlsAnnotationUtils.isNotEmptyStr(dbSheetConfig.getParentClass())) {
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
                shhetClassFile.addAttribute(classAnnotationAttr);

                //add field
                for (DbCellConfig cellField : dbSheetConfig.getDbCellConfigs()) {
                    // 创建一个新的字段
                    CtField ctCellField = new CtField(pool.get(cellField.getFieldType()), cellField.getFieldName(), sheetClass);
                    CtField hadAddField = sheetClass.getField(cellField.getFieldName());
                    if (hadAddField != null) {
                        continue;
                    }

                    ctCellField.setModifiers(Modifier.PUBLIC);
                    //add XlsCell annotation
                    AnnotationsAttribute fieldAttr = new AnnotationsAttribute(sheetClassConstPool, AnnotationsAttribute.visibleTag);
                    Annotation xlsCellAnnotation = new Annotation(XlsCell.class.getName(), sheetClassConstPool);
                    xlsCellAnnotation.addMemberValue("index", new IntegerMemberValue(cellField.getIndex(), sheetClassConstPool));
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
                    if (XlsAnnotationUtils.isNotEmptyStr(cellField.getInnerSheetToClass())) {
                        xlsCellAnnotation.addMemberValue("innerSheetToClass", new StringMemberValue(cellField.getInnerSheetToClass(), sheetClassConstPool));
                    }
                    if (XlsAnnotationUtils.isNotEmptyStr(cellField.getInnerSheetToField())) {
                        xlsCellAnnotation.addMemberValue("innerSheetToField", new StringMemberValue(cellField.getInnerSheetToField(), sheetClassConstPool));
                    }
                    if (cellField.getInnerSheetRowCount() > 0) {
                        xlsCellAnnotation.addMemberValue("innerSheetRowCount", new IntegerMemberValue(cellField.getInnerSheetRowCount(), sheetClassConstPool));
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
                        xlsCellAnnotation.addMemberValue("columnWeight", new IntegerMemberValue(cellField.getColumnWeight(), sheetClassConstPool));
                    }

                    fieldAttr.addAnnotation(xlsCellAnnotation);

                    // 将注解属性添加到字段
                    ctCellField.getFieldInfo().addAttribute(fieldAttr);
                    sheetClass.addField(ctCellField);
                }

                sheetClass.writeFile("./target/dbconfig/");
            }
        }


        for (DbExcelConfig loopExcelConfig : dbExcelConfigs) {
            CtClass mainContainerClass = pool.makeClass(loopExcelConfig.getPackageName() + "." + loopExcelConfig.getClassName());
            mainContainerClass.setModifiers(Modifier.PUBLIC);
            ConstPool mainContainerClassClassPool = mainContainerClass.getClassFile().getConstPool();
            //add XLSExcel annotation
            AnnotationsAttribute fieldAttr = new AnnotationsAttribute(mainContainerClassClassPool, AnnotationsAttribute.visibleTag);
            Annotation xlsExcel = new Annotation(XlsExcel.class.getName(), mainContainerClassClassPool);
            xlsExcel.addMemberValue("title", new StringMemberValue(loopExcelConfig.getTitle(), mainContainerClassClassPool));
            ArrayMemberValue categoryArray = new ArrayMemberValue(mainContainerClassClassPool);
            MemberValue[] array = Arrays.stream(loopExcelConfig.getCategory())
                    .map(p -> new StringMemberValue(p, mainContainerClassClassPool))
                    .toList()
                    .toArray(new MemberValue[]{});
            categoryArray.setValue(array);
            xlsExcel.addMemberValue("category", categoryArray);

            // add sheet field
            for (DbSheetConfig dbSheetConfig : loopExcelConfig.getDbSheetConfigs()) {
                // 创建一个新的字段
                CtField ctField = new CtField(pool.get(loopExcelConfig.getPackageName() + "." + dbSheetConfig.getClassName()), dbSheetConfig.getClassName(), mainContainerClass);
                ctField.setModifiers(Modifier.PUBLIC);
            }
            mainContainerClass.writeFile("./target/dbconfig/");
        }
    }
}
