package org.example;

//import org.apache.poi.util.IOUtils;
import org.apache.poi.util.IOUtils;
import org.example.test.vo.MainVo;
import org.example.utils.XlsGlobalUtils;
import org.example.xls.config.XlsCellConfig;
import org.example.xls.config.XlsSheetConfig;

import javax.print.DocFlavor;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    /**
     *    * 动态设置注解内容，module java 需要配置 JAVA_TOOL_OPTIONS: --add-opens=java.base/sun.reflect.annotation=ALL-UNNAMED
     *      * --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED  --add-opens=java.base/java.lang.reflect=ALL-UNNAMED
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

//        List<XlsExcel> allXlsExcels = XlsAnnotationUtils.getAllInitAnnotations(new String[]{"org.example"}, XlsExcel.class);
//        for (XlsExcel allXlsExcel : allXlsExcels) {
//            XlsAnnotationUtils.setAnnotationValue(allXlsExcel,"title","1111111111");
//        }
//        for (XlsExcel xlsExcel : allXlsExcels) {
//            System.out.println(xlsExcel);
//        }

        XlsGlobalUtils.loadStyleAndValidation(new String[]{"org.example"});
        XlsGlobalUtils.loadAnnotationParse(new String[]{"org.example"});
        XlsGlobalUtils.init(new String[]{"org.example"});
//        System.out.println(XlsGlobalUtils.allCategories);
//        System.out.println(XlsGlobalUtils.allExcelConfigs);
//        System.out.println(XlsGlobalUtils.allSheetConfigs);
//        System.out.println(XlsGlobalUtils.allPropertyConfigs);
//        Set<Field> allFields = ReflectionUtils.getAllFields(MainVo.class, (f)->true);
//        System.out.println(allFields);

        XlsGlobalUtils.allExcelConfigs.forEach((k,v)-> System.out.println(v));
        List<XlsCellConfig> xlsCellConfigs = XlsGlobalUtils.allExcelConfigs.values().iterator().next().sheetConfigs().get(0).xlsCellConfigs();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XlsGlobalUtils.getXlsTemplate(MainVo.class,outputStream);

        try(FileOutputStream fileOutputStream = new FileOutputStream("./target/tempate.xlsx")){
            fileOutputStream.write(outputStream.toByteArray());
        }

        byte[] byteArray = IOUtils.toByteArray(new FileInputStream("./target/tempate-target.xlsx"));
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        Object o = XlsGlobalUtils.loadData(byteArray,errorStream);
        if(errorStream.size() > 0){
            FileOutputStream fileOutputStream = new FileOutputStream("./target/tempate-target-error.xlsx");
            fileOutputStream.write(errorStream.toByteArray());
            fileOutputStream.close();
        }
        System.out.println(o);

        List<Map<XlsSheetConfig, Object>> businessObj = XlsGlobalUtils.transform(o, MainVo.class);

        System.out.println(businessObj);

        XlsGlobalUtils.buildStruct(businessObj);

        System.out.println(businessObj);
        
        List exportData = new ArrayList();
        for (Map<XlsSheetConfig, Object> xlsSheetConfigObjectMap : businessObj) {
            for (XlsSheetConfig xlsSheetConfig : xlsSheetConfigObjectMap.keySet()) {
                exportData.add(xlsSheetConfigObjectMap.get(xlsSheetConfig));
            }
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XlsGlobalUtils.export(exportData,MainVo.class,byteArrayOutputStream);

        FileOutputStream fileOutputStream = new FileOutputStream("./target/export.xlsx");
        fileOutputStream.write(byteArrayOutputStream.toByteArray());
        fileOutputStream.close();

    }
}