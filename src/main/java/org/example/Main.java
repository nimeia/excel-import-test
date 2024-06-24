package org.example;

import org.example.test.vo.MainVo;
import org.example.utils.XlsGlobalUtils;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) throws IOException {

//        List<XlsExcel> allXlsExcels = XlsAnnotationUtils.getAllInitAnnotations(new String[]{"org.example"}, XlsExcel.class);
//        for (XlsExcel allXlsExcel : allXlsExcels) {
//            XlsAnnotationUtils.setAnnotationValue(allXlsExcel,"title","1111111111");
//        }
//        for (XlsExcel xlsExcel : allXlsExcels) {
//            System.out.println(xlsExcel);
//        }
        XlsGlobalUtils.init(new String[]{"org.example"});
//        System.out.println(XlsGlobalUtils.allCategories);
//        System.out.println(XlsGlobalUtils.allExcelConfigs);
//        System.out.println(XlsGlobalUtils.allSheetConfigs);
//        System.out.println(XlsGlobalUtils.allPropertyConfigs);
//        Set<Field> allFields = ReflectionUtils.getAllFields(MainVo.class, (f)->true);
//        System.out.println(allFields);

        XlsGlobalUtils.getXlsTemplate(MainVo.class);

//        byte[] byteArray = IOUtils.toByteArray(new FileInputStream("./test.xlsx"));
//        Object o = XlsGlobalUtils.loadData(byteArray);
//        System.out.println(o);

    }
}