package org.example.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.vo.*;
import org.example.xls.config.*;
import org.reflections.ReflectionUtils;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

public class XlsGlobalUtils {

    public static final String EXCEL_BASE_INFO_SHEET = "base-info";
    /**
     * 格式：<br/>
     * <b>type1 </b>: [{key:value},{kye:value}] <br/>
     * <b>type2</b> : [{key:value},{kye:value}] <br/>
     */
    public static Map<String, List<Map<String, String>>> allCategories = new HashMap<String, List<Map<String, String>>>();

    public static Map<Class<?>, byte[]> allTemplateCaches = new HashMap<>();

    public static Map<Class<?>, XlsExcelConfig> allExcelConfigs = new HashMap<>();

    public static Map<String, HeadStyle> headStyleMap = new HashMap<>();

    public static Map<String, ColumnValidation> validationMap = new HashMap<>();


    /**
     * 加载样式与校验配置
     * @param basePackages
     */
    public static void loadStyleAndValidation(String[] basePackages){
        List<Class<?>> allStyles = XlsAnnotationUtils.getAllClassWithAnnotation(basePackages,XlsStyleHead.class);
        for (Class<?> styleClass : allStyles) {
            if(HeadStyle.class.isAssignableFrom(styleClass)){
                HeadStyle o = null;
                try {
                    o = (HeadStyle)styleClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                headStyleMap.put(o.getName(),o);
            }
        }

        List<Class<?>> allValidations = XlsAnnotationUtils.getAllClassWithAnnotation(basePackages,XlsValidation.class);
        for (Class<?> validation : allValidations) {
            if(ColumnValidation.class.isAssignableFrom(validation)){
                ColumnValidation o = null;
                try {
                    o = (ColumnValidation)validation.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                validationMap.put(o.getName(),o);
            }
        }
    }


    /**
     * 初始化excel导入配置
     *
     * @param basePackages
     */
    public static void init(String[] basePackages) {
        Map<String, Boolean> dubCheckMap = new HashMap<>();

        // init category data
        List<Class<?>> allXlsExcel = XlsAnnotationUtils.getAllClassWithAnnotation(basePackages, XlsExcel.class);
        for (Class<?> excelClass : allXlsExcel) {
            XlsExcel xlsExcel = excelClass.getAnnotation(XlsExcel.class);
            XlsExcelConfig xlsExcelConfig = new XlsExcelConfig(xlsExcel);
            XlsAnnotationUtils.setFieldValue(xlsExcelConfig, "bindClass", excelClass);
            //XlsAnnotationUtils.setAnnotationValue(xlsExcel,"bindClass", excelClass);
            allExcelConfigs.put(excelClass, xlsExcelConfig);
            String[] categoryDefs = xlsExcel.category();
            String fullCategoryName = String.join(",", categoryDefs);
            if (dubCheckMap.containsKey(fullCategoryName)) {
                throw new RuntimeException("重复的配置类型: " + fullCategoryName);
            } else {
                dubCheckMap.put(fullCategoryName, true);
            }
            for (String category : categoryDefs) {
                String[] split = category.split("\\|");
                String type = split[0];
                List<Map<String, String>> categoryList = allCategories.computeIfAbsent(type, k -> new ArrayList<>());
                Map<String, String> newCategory = Map.of(split[1], split[2]);
                if (!categoryList.contains(newCategory)) {
                    categoryList.add(newCategory);
                }
            }

            //init sheet data
            List<XlsSheetConfig> xlsSheets = xlsExcelConfig.sheetConfigs();
            if (excelClass.isAnnotationPresent(XlsSheet.class)) {
                //特殊情况，excle 只有单个sheet ,如何对象是自己
                XlsSheet xlsSheet = excelClass.getAnnotation(XlsSheet.class);
                XlsSheetConfig xlsSheetConfig = new XlsSheetConfig(xlsSheet);
                xlsSheetConfig.fieldRealTypeClass(excelClass);
                xlsSheetConfig.ownerClass(excelClass);

                xlsSheetConfig.field(null);
                xlsSheetConfig.setMethod(null);
                xlsSheetConfig.getMethod(null);

                xlsSheets.add(xlsSheetConfig);


                initSheetCells(excelClass, xlsSheetConfig);
            } else {
                List<Field> allFields = new ArrayList<>(ReflectionUtils.getAllFields(excelClass, (f) -> {
                    if (f.isAnnotationPresent(XlsIgnore.class)) return false;
                    f.setAccessible(true);
                    Class aClass = XlsAnnotationUtils.fieldCollectionRealType(f);
                    if (aClass != null) {
                        return aClass.isAnnotationPresent(XlsSheet.class);
                    } else {
                        return f.getType().isAnnotationPresent(XlsSheet.class);
                    }
                }));

                for (Field sheetField : allFields) {
                    if (sheetField.isAnnotationPresent(XlsIgnore.class)) continue;
                    Class<?> sheetFieldClassType = sheetField.getType();
                    if (Set.class.isAssignableFrom(sheetFieldClassType) || Map.class.isAssignableFrom(sheetFieldClassType)) {
                        //set map not support
                        throw new RuntimeException(" set map not supported now ");
                    }
                    Class<?> fileRealClass = XlsAnnotationUtils.fieldCollectionRealType(sheetField);
                    if(fileRealClass == null) fileRealClass = sheetField.getType();
                    XlsSheet xlsSheet = fileRealClass.getAnnotation(XlsSheet.class);

                    XlsSheetConfig xlsSheetConfig = new XlsSheetConfig(xlsSheet);
                    xlsSheets.add(xlsSheetConfig);

                    xlsSheetConfig.fieldRealTypeClass(fileRealClass);
                    xlsSheetConfig.field(sheetField);
                    xlsSheetConfig.ownerClass(excelClass);
                    xlsSheetConfig.setMethod(XlsAnnotationUtils.getSetterMethod(excelClass,sheetField.getName()));
                    xlsSheetConfig.getMethod(XlsAnnotationUtils.getGetterMethod(excelClass,sheetField.getName()));

                    if(sheetField.isAnnotationPresent(XlsIndex.class)){
                        xlsSheetConfig.index(sheetField.getAnnotation(XlsIndex.class).index());
                    };

                    if(Collection.class.isAssignableFrom(sheetFieldClassType)){
                        xlsSheetConfig.isCollection(true);
                    }
                    //初始化cell 配置
                    Class<?> realClass = XlsAnnotationUtils.fieldCollectionRealType(sheetField);
                    if(realClass == null) realClass = sheetField.getType();
                    initSheetCells(realClass, xlsSheetConfig);
                }
            }

            //检查 index 是否重复
            xlsSheets.forEach(sheet ->{
                if( xlsSheets.stream().filter(e -> e.index() == sheet.index()).count() >1 ) throw new RuntimeException("sheet index 重复，请使用@XlsIndex 重置");
            });
            //xlsSheets 排序
            Collections.sort(xlsSheets, (e1, e2) -> e1.index() - e2.index());

            //
            for (XlsSheetConfig xlsSheet : xlsSheets) {
                int headRow = xlsSheet.xlsCellConfigs().stream().map(cell->cell.headTitle().length).max((e1, e2) -> e1.compareTo(e2)).get();
                XlsAnnotationUtils.setFieldValue(xlsSheet, "headRow", headRow);
                Collections.sort(xlsSheet.xlsCellConfigs(), (e1, e2) -> e1.index() - e2.index());
            }
        }
    }

    /**
     * 返回模板缓存
     *
     * @param clazz
     * @return
     */
    public static byte[] getXlsTemplate(Class<?> clazz) {
        return allTemplateCaches.computeIfAbsent(clazz, k -> {

            Workbook workbook = new XSSFWorkbook();

            XlsExcelConfig xlsExcel = allExcelConfigs.get(clazz);
            if (xlsExcel == null) return null;

            List<XlsSheetConfig> xlsSheetList = xlsExcel.sheetConfigs();//allSheetConfigs.get(clazz);
            if (xlsSheetList == null) return null;

            for (XlsSheetConfig xlsSheet : xlsSheetList) {
                Sheet dataSheet = workbook.createSheet(xlsSheet.title());
                int sheetIndex = workbook.getSheetIndex(dataSheet);
                if (xlsSheet.sheetActive()) {
                    workbook.setActiveSheet(sheetIndex);
                }
                if (xlsSheet.hidden()) {
                    workbook.setSheetHidden(sheetIndex, true);
                }

                //cell
                List<XlsCellConfig> xlsCells = xlsSheet.xlsCellConfigs();//allCellConfigs.get(xlsSheet.bindClass());

                for (int j = 0; j < xlsSheet.headRow(); j++) {
                    Row row = dataSheet.createRow(j);
                    for (int i = 0; i < xlsCells.size(); i++) {
                        Cell cell = row.createCell(i);
                        XlsCellConfig xlsCell = xlsCells.get(i);
                        int index = (xlsSheet.headRow() - xlsCell.headTitle().length) - j >= 0 ? 0 : j - (xlsSheet.headRow() - xlsCell.headTitle().length);
                        String value =/*xlsCell.bindField()+"-"+xlsCell.index()+"-"+*/xlsCell.headTitle()[index];
                        cell.setCellValue(value);

                        if (cell.getCellComment() == null) {
                            // 创建绘图对象
                            Drawing<?> drawing = dataSheet.createDrawingPatriarch();

                            // 创建批注锚点
                            CreationHelper factory = workbook.getCreationHelper();
                            ClientAnchor anchor = factory.createClientAnchor();
                            anchor.setCol1(cell.getColumnIndex()); // 批注开始的列
                            anchor.setRow1(cell.getRowIndex()); // 批注开始的行
                            anchor.setCol2(cell.getColumnIndex() + 2);
                            anchor.setRow2(cell.getRowIndex() + 2);

                            // 创建批注对象
//                          if(j == xlsSheet.headRow()-1){
                                Comment comment = drawing.createCellComment(anchor);
                                RichTextString str = null;
                                if(xlsCell.innerSheetField()!=null){
                                    str = factory.createRichTextString(xlsCell.innerSheetField().getName());
                                }else{
                                    str = factory.createRichTextString(xlsCell.field().getName());
                                }

                                comment.setString(str);
                                comment.setAuthor("X");
                                comment.setVisible(false);
                                // 设置批注到单元格
                                cell.setCellComment(comment);
//                           }

                        }
                        if (!xlsCell.headStyle().equals("")) {
                            // 创建单元格样式
                            try {
                                HeadStyle headStyle = headStyleMap.get(xlsCell.headStyle());
                                if(headStyle != null){
                                    CellStyle cellStyle = headStyle.headStyle(dataSheet);
                                    // 应用样式到单元格
                                    cell.setCellStyle(cellStyle);
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if(j == xlsSheet.headRow()-1){
                            //设置列格式
                            if (!xlsCell.validation().equals("")) {
                                // 创建单元格样式
                                try {
                                    ColumnValidation columnValidation = validationMap.get(xlsCell.validation());
                                    if(columnValidation!=null){
                                        columnValidation.validation(workbook,dataSheet,xlsSheet.headRow(),1048576,cell.getColumnIndex(),cell.getColumnIndex());
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
                mergeSameCells(dataSheet);
            }

            {
                Sheet hiddenSheet = workbook.createSheet(EXCEL_BASE_INFO_SHEET);
                int sheetIndex = workbook.getSheetIndex(hiddenSheet);
                workbook.setSheetHidden(sheetIndex, true);
                Row row = hiddenSheet.createRow(0);
                Cell cell = row.createCell(0);
                cell.setCellValue(clazz.getName());
                hiddenSheet.autoSizeColumn(0);
            }

            // 将工作簿写入文件
            try (FileOutputStream fileOut = new FileOutputStream("./test.xlsx")) {
                workbook.write(fileOut);
            } catch (IOException e) {
                e.printStackTrace();
            }


//            workbook.close();

            return null;
        });
    }

    /**
     * 加载数据
     *
     * @param excelBytes
     * @return
     */
    public static Object loadData(byte[] excelBytes) {
        try {
            Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes));
            Sheet sheet = workbook.getSheet(EXCEL_BASE_INFO_SHEET);
            String className = "";
            if (sheet != null) {
                className = sheet.getRow(0).getCell(0).getStringCellValue();
            }
            if (!"".equals(className)) {
                Class<?> clazz = Class.forName(className);
                XlsExcelConfig xlsExcel = allExcelConfigs.get(clazz);
                if (xlsExcel == null) return null;
                List<XlsSheetConfig> xlsSheetList = xlsExcel.sheetConfigs();//allSheetConfigs.get(clazz);
                if (xlsSheetList == null) return null;

                Object result = xlsExcel.bindClass().getDeclaredConstructor().newInstance();

                boolean sameFlag = false;
                if (xlsExcel.bindClass().isAnnotationPresent(XlsSheet.class)) {
                    sameFlag = true;
                }

                for (XlsSheetConfig xlsSheet : xlsSheetList) {
                    Sheet dataSheet = workbook.getSheet(xlsSheet.title());
//                    List<XlsCellConfig> xlsCells = xlsSheet.xlsCellConfigs();//allCellConfigs.get(xlsSheet.bindClass());
                    int lastRowNum = dataSheet.getLastRowNum();
                    List datalist = new ArrayList<>();

                    //header 处理
                    Row row = dataSheet.getRow(xlsSheet.headRow() - 1);
                    int headLastCellNum = row.getLastCellNum();

                    List<XlsCellConfig> xlsCellList = xlsSheet.xlsCellConfigs();
                    //检查是否有乱序
                    for (int i = 0; i < headLastCellNum; i++) {
                        String fileName = row.getCell(i).getCellComment().getString().getString();
                        XlsCellConfig xlsCellConfig = xlsCellList.get(i);
                        if (xlsCellConfig.innerSheetField() != null) {
                            if (!xlsCellConfig.innerSheetField().getName().equals(fileName)) {
                                throw new RuntimeException("模板配置字段顺序不一致，请检查！" + fileName);
                            }
                        } else {
                            if (!xlsCellConfig.field().getName().equals(fileName)) {
                                throw new RuntimeException("模板配置字段顺序不一致，请检查！" + fileName);
                            }
                        }
                    }

                    for (int rowIndex = xlsSheet.headRow(); rowIndex <= lastRowNum; rowIndex++) {
                        Row currentRowData = dataSheet.getRow(rowIndex);
                        int lastCellNum = currentRowData.getLastCellNum();
                        Object mainSheetData = xlsSheet.fieldRealTypeClass().getDeclaredConstructor().newInstance();
                        for (int i = 0; i < lastCellNum; i++) {
                            //Field field = fieldList.get(i);
                            XlsCellConfig xlsCellConfig = xlsCellList.get(i);

                            Object data = mainSheetData;
                            if (xlsCellConfig.innerSheetToClass() != void.class) {
                                //主表属性是 collection 还是单对象
                                if (Collection.class.isAssignableFrom(xlsCellConfig.field().getType())) {
                                    Object co = xlsCellConfig.field().get(mainSheetData);
                                    if (co == null) {
                                        //o is list
                                        Object list = xlsCellConfig.field().getType().getDeclaredConstructor().newInstance();
                                        xlsCellConfig.field().set(mainSheetData, list);
                                        co = list;
                                    }
                                    // 计算当前是第几个
                                    if (xlsCellConfig.innerSheetIndex() > ((Collection<?>) co).size()) {
                                        data = xlsCellConfig.fieldRealTypeClass().getDeclaredConstructor().newInstance();
                                        ((Collection) co).add(data);
                                    } else {
                                        data = ((Collection<?>) co).toArray()[xlsCellConfig.innerSheetIndex()];
                                    }
                                    // ((Collection)co).add(data);
                                } else {
                                    Object co = xlsCellConfig.field().get(mainSheetData);
                                    if (co == null) {
                                        Object o = xlsCellConfig.fieldRealTypeClass().getDeclaredConstructor().newInstance();
                                        co = o;
                                        xlsCellConfig.field().set(mainSheetData, co);
                                    }
                                    data = co;
                                }
                            }
                            Field field = xlsCellConfig.innerSheetField()!=null ? xlsCellConfig.innerSheetField() : xlsCellConfig.field();
                            Class<?> aClass = xlsCellConfig.innerSheetField()!=null ? xlsCellConfig.innerSheetField().getType() :  xlsCellConfig.fieldRealTypeClass();
                            Method setMethod = xlsCellConfig.innerSheetSetMethod()!=null ? xlsCellConfig.innerSheetSetMethod() :xlsCellConfig.setMethod();
                            // System.out.println(String.format("get cell %s,%s for %s", rowIndex, i, field));
                            // dataMap.put(headFiledNames.get(i), currentRowData.getCell(i).getStringCellValue());
                            Object cellValue = ExcelUtil.getCellValue(currentRowData.getCell(i));
                            if (cellValue != null) {

                                if (cellValue.getClass() == aClass) {
                                    if (setMethod != null) {
                                        setMethod.invoke(data, cellValue);
                                    } else {
                                        field.set(data, cellValue);
                                    }
                                } else if (cellValue instanceof String) {
                                    if (setMethod != null) {
                                        setMethod.invoke(data, StringToTypeConverterUtils.convert(cellValue.toString(), aClass));
                                    } else {
                                        field.set(data, StringToTypeConverterUtils.convert(cellValue.toString(), aClass));
                                    }
                                } else {
                                    if (setMethod != null) {
                                        setMethod.invoke(data, StringToTypeConverterUtils.convert(cellValue.toString(), aClass));
                                    } else {
                                        field.set(data, StringToTypeConverterUtils.convert(cellValue.toString(), aClass));
                                    }
                                }
                            }
                        }
                        datalist.add(mainSheetData);
                    }
                    if (sameFlag) {
                        return datalist;
                    }
                    // Field field = allSheetFields.get(xlsSheet);
                    Field field = xlsSheet.field();
                    Method setMethod = xlsSheet.setMethod();
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        if (setMethod != null) {
                            setMethod.invoke(result, datalist);
                        } else {
                            field.set(result, datalist);
                        }
                    } else {
                        if (datalist != null && datalist.size() > 0) {
                            if (setMethod != null) {
                                setMethod.invoke(result, datalist.get(0));
                            } else {
                                field.set(result, datalist.get(0));
                            }
                        }
                    }
                }

                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转换成业务对象
     *
     * @param o
     */
    public static Object transform(Object o, Class<?> clazz) {
        if (!clazz.isAnnotationPresent(XlsExcel.class)) {
            return null;
        }
        XlsExcelConfig xlsExcel = allExcelConfigs.get(clazz);
        List<XlsSheetConfig> sheetConfigs = xlsExcel.sheetConfigs();
        List<Map<XlsSheetConfig, Object>> result = new ArrayList<>();
        if (Collection.class.isAssignableFrom(o.getClass())) {
            //这种场景只能是单表，
            return doTransform(xlsExcel.sheetConfigs().get(0), o);
        } else {
//            Set<Field> allFields = ReflectionUtils.getAllFields(clazz,
//                    (f) -> f.getType().isAnnotationPresent(XlsSheet.class) && !f.getType().isAnnotationPresent(XlsIgnore.class));
//            allFields.forEach(field -> {
//
//            });
            sheetConfigs.forEach(sheetConfig -> {
                Object target = null;
                if (sheetConfig.getMethod() != null) {
                    try {
                        target = sheetConfig.getMethod().invoke(o);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        target = sheetConfig.field().get(o);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

                Map<XlsSheetConfig, Object> e = doTransform(sheetConfig, target);
                if (e != null && !e.isEmpty()) result.add(e);
            });
        }


        return result;
    }

    private static Map<XlsSheetConfig, Object> doTransform(XlsSheetConfig sheetConfig, Object target) {
        try {
            if (target == null) {
                return null;
            }
            if (sheetConfig.toClass() == null) {
                return Map.of(sheetConfig, target);
            }
            List result = new ArrayList<>();

            int loopTime = (target instanceof Collection<?> ? ((Collection<?>) target).size() : 1);
            boolean collectionFlag = (target instanceof Collection<?>);
            List<?> listObj = collectionFlag ? Arrays.asList(((Collection<?>) target).toArray()) : new ArrayList<>();
            Class<?> targetClass = sheetConfig.toClass();
            if (targetClass == void.class) {
                return Map.of(sheetConfig, target);
            }

            for (int i = 0; i < loopTime; i++) {
                Object loopObj = null;
                if (!collectionFlag) {
                    loopObj = target;
                } else {
                    loopObj = listObj.get(i);
                }
                if (loopObj == null) continue;
                Object targetObj = targetClass.getDeclaredConstructor().newInstance();
                Object finalLoopObj = loopObj;
                sheetConfig.xlsCellConfigs().forEach(cellConfig -> {
                    if (cellConfig.innerSheetField() != null) {
                        int index = cellConfig.innerSheetIndex();
                        Object innerSheetObj = XlsAnnotationUtils.initField(targetObj,
                                cellConfig.targetField(),
                                cellConfig.targetSetMethod(),
                                cellConfig.targetGetMethod(),
                                index);
                        Object resultO = XlsAnnotationUtils.getFieldValue(finalLoopObj,
                                cellConfig.field(),
                                cellConfig.getMethod(),
                                cellConfig.innerSheetField(),
                                cellConfig.innerSheetGetMethod(),
                                index);
                        XlsAnnotationUtils.setFileValue(innerSheetObj,
                                resultO,
                                cellConfig.innerSheetField(),
                                cellConfig.innerSheetSetMethod(),
                                cellConfig.innerSheetTargetField(),
                                cellConfig.innerSheetTargetSetMethod(),
                                index);
                    } else {
                        Object resultO = XlsAnnotationUtils.getFieldValue(finalLoopObj,
                                cellConfig.field(),
                                cellConfig.getMethod(),
                                cellConfig.innerSheetField(),
                                cellConfig.innerSheetGetMethod(),
                                null);
                        XlsAnnotationUtils.setFileValue(targetObj,
                                resultO,
                                cellConfig.targetField(),
                                cellConfig.targetSetMethod(),
                                cellConfig.innerSheetTargetField(),
                                cellConfig.innerSheetTargetSetMethod(),
                                null);
                    }
                });
                result.add(targetObj);
            }

            if (!collectionFlag && !result.isEmpty()) return Map.of(sheetConfig, result.get(0));
            return Map.of(sheetConfig, result);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成cell 配置
     * @param xlsSheetConfig
     * @return
     */
    private static int initSheetCells(Class sheetFieldRealClass, XlsSheetConfig xlsSheetConfig){
        if(!sheetFieldRealClass.isAnnotationPresent(XlsSheet.class)) return -1;

        List<Field> allFieldsInSheets = new ArrayList<>(ReflectionUtils.getAllFields(sheetFieldRealClass, f -> {
            f.setAccessible(true);
            return f.isAnnotationPresent(XlsCell.class) && !f.isAnnotationPresent(XlsIgnore.class);
        }));
        //计算cell的index 是否重复
        boolean sameIndexFlag = allFieldsInSheets.stream().anyMatch(field ->
                allFieldsInSheets.stream().anyMatch(innerField -> innerField != field && innerField.getAnnotation(XlsCell.class).index() == field.getAnnotation(XlsCell.class).index())
        );
        if(sameIndexFlag){
            throw new RuntimeException(sheetFieldRealClass.getName() + " cell 中有重复的index ，请检查 ");
        }
        //需要先排序一次，避免innerSheet 中cell index计算错误
        Collections.sort(allFieldsInSheets,(f1,f2)->f1.getAnnotation(XlsCell.class).index() - f2.getAnnotation(XlsCell.class).index());

        Class<?> sheetToClass = xlsSheetConfig.toClass();
        int cellIndex = -1;
        for (int sheetFieldLoopIndex = 0; sheetFieldLoopIndex < allFieldsInSheets.size(); sheetFieldLoopIndex++) {
            cellIndex ++;
            Field fieldInSheet = allFieldsInSheets.get(sheetFieldLoopIndex);
            if(Set.class.isAssignableFrom(fieldInSheet.getType())
                    || Map.class.isAssignableFrom(fieldInSheet.getType())) {
                throw new RuntimeException("Set Map not support yet!");
            }

            Class fieldRealClassInSheet = XlsAnnotationUtils.fieldCollectionRealType(fieldInSheet);
            if(fieldRealClassInSheet == null) fieldRealClassInSheet = fieldInSheet.getType();

            XlsCell xlsCellAnnotation = fieldInSheet.getAnnotation(XlsCell.class);
            //判断是否普通数据类型且内部包括cell
            if(!CommonTypeCheckerUtils.isCommonType(fieldRealClassInSheet)
                    && XlsAnnotationUtils.fieldContainsXlsCell(fieldRealClassInSheet)){
                cellIndex = initInnerSheetCells(cellIndex,fieldInSheet,xlsCellAnnotation,fieldRealClassInSheet,xlsSheetConfig);
            }else{
                //普通cell
                XlsCellConfig xlsCellConfig = new XlsCellConfig(xlsCellAnnotation);
                xlsCellConfig.index(cellIndex);
                xlsCellConfig.fieldRealTypeClass(fieldRealClassInSheet);
                xlsCellConfig.field(fieldInSheet);
                xlsCellConfig.getMethod(XlsAnnotationUtils.getGetterMethod(sheetFieldRealClass,fieldInSheet.getName()));
                xlsCellConfig.setMethod(XlsAnnotationUtils.getSetterMethod(sheetFieldRealClass,fieldInSheet.getName()));
                String toFieldName = xlsCellAnnotation.toField();
                if(!XlsAnnotationUtils.isNotEmptyStr(toFieldName)){
                    toFieldName = fieldInSheet.getName();
                }
                xlsCellConfig.targetField(XlsAnnotationUtils.getFieldByName(sheetToClass,toFieldName));
                xlsCellConfig.targetSetMethod(XlsAnnotationUtils.getSetterMethod(sheetToClass,toFieldName));
                xlsCellConfig.targetGetMethod(XlsAnnotationUtils.getGetterMethod(sheetToClass,toFieldName));

                if (xlsCellConfig.headTitle().length == 0) {
                    xlsCellConfig.headTitle(new String[]{fieldInSheet.getName()});
                }
                xlsSheetConfig.xlsCellConfigs().add(xlsCellConfig);
            }
        }
        return 0;
    }

    private static int initInnerSheetCells(int startCellIndex,
                                    Field fieldInMainSheet,
                                    XlsCell xlsCellAnnotationInMainSheet,
                                    Class fieldRealClassInMainSheet,
                                    XlsSheetConfig xlsSheetConfig){
        // innerSheet
        int innerSheetLoopTime = 1;
        boolean innerFieldIsList = false;
        if(List.class.isAssignableFrom(fieldInMainSheet.getType())){
            innerFieldIsList = true;
            innerSheetLoopTime = xlsCellAnnotationInMainSheet.innerSheetRowCount();
        }
        List<Field> allFieldsInInnerSheets = new ArrayList<>(ReflectionUtils.getAllFields(fieldRealClassInMainSheet, f -> {
            f.setAccessible(true);
            return f.isAnnotationPresent(XlsCell.class) && !f.isAnnotationPresent(XlsIgnore.class);
        }));
        //计算cell的index 是否重复
        boolean sameIndexFlagForInnerSheet = allFieldsInInnerSheets.stream().anyMatch(field ->
                allFieldsInInnerSheets.stream().anyMatch(innerField -> innerField != field && innerField.getAnnotation(XlsCell.class).index() == field.getAnnotation(XlsCell.class).index())
        );
        if(sameIndexFlagForInnerSheet){
            throw new RuntimeException(fieldRealClassInMainSheet.getName() + " cell 中有重复的index ，请检查 ");
        }
        //需要先排序一次，避免innerSheet 中cell index计算错误
        Collections.sort(allFieldsInInnerSheets,(f1,f2)->f1.getAnnotation(XlsCell.class).index() - f2.getAnnotation(XlsCell.class).index());

        // index 回退一格
        startCellIndex --;
        for (int innerCellLoopIndex = 0; innerCellLoopIndex < innerSheetLoopTime; innerCellLoopIndex++) {
            for (Field innerSheetField : allFieldsInInnerSheets) {
                startCellIndex ++;

                XlsCell xlsCellAnnotationInInnerSheet = innerSheetField.getAnnotation(XlsCell.class);
                XlsCellConfig xlsCellConfig = new XlsCellConfig(xlsCellAnnotationInInnerSheet);
                xlsCellConfig.index(startCellIndex);

                xlsCellConfig.fieldRealTypeClass(fieldRealClassInMainSheet);
                xlsCellConfig.field(fieldInMainSheet);
                xlsCellConfig.getMethod(XlsAnnotationUtils.getGetterMethod(xlsSheetConfig.fieldRealTypeClass(),fieldInMainSheet.getName()));
                xlsCellConfig.setMethod(XlsAnnotationUtils.getSetterMethod(xlsSheetConfig.fieldRealTypeClass(),fieldInMainSheet.getName()));
                String toFieldName = xlsCellAnnotationInMainSheet.toField();
                if(!XlsAnnotationUtils.isNotEmptyStr(toFieldName)){
                    toFieldName = fieldInMainSheet.getName();
                }
                xlsCellConfig.targetField(XlsAnnotationUtils.getFieldByName(xlsSheetConfig.toClass(),toFieldName));
                xlsCellConfig.targetSetMethod(XlsAnnotationUtils.getSetterMethod(xlsSheetConfig.toClass(),toFieldName));
                xlsCellConfig.targetGetMethod(XlsAnnotationUtils.getGetterMethod(xlsSheetConfig.toClass(),toFieldName));

                if (xlsCellConfig.headTitle().length == 0) {
                    xlsCellConfig.headTitle(new String[]{fieldInMainSheet.getName()});
                }

                //设置inner Sheet 部分
                xlsCellConfig.innerSheetIndex(innerCellLoopIndex);
                xlsCellConfig.isArray(innerFieldIsList);
                String innerFieldToName = xlsCellAnnotationInInnerSheet.toField();
                if(!XlsAnnotationUtils.isNotEmptyStr(innerFieldToName)){
                    innerFieldToName = innerSheetField.getName();
                }
                xlsCellConfig.innerSheetToClass(xlsCellAnnotationInMainSheet.innerSheetToClass());
                xlsCellConfig.innerSheetField(innerSheetField);
                xlsCellConfig.innerSheetGetMethod(XlsAnnotationUtils.getGetterMethod(fieldRealClassInMainSheet,innerSheetField.getName()));
                xlsCellConfig.innerSheetSetMethod(XlsAnnotationUtils.getSetterMethod(fieldRealClassInMainSheet,innerSheetField.getName()));
                xlsCellConfig.innerSheetTargetField(XlsAnnotationUtils.getFieldByName(xlsCellAnnotationInMainSheet.innerSheetToClass(),innerFieldToName));
                xlsCellConfig.innerSheetTargetGetMethod(XlsAnnotationUtils.getGetterMethod(xlsCellAnnotationInMainSheet.innerSheetToClass(),innerFieldToName));
                xlsCellConfig.innerSheetTargetSetMethod(XlsAnnotationUtils.getSetterMethod(xlsCellAnnotationInMainSheet.innerSheetToClass(),innerFieldToName));

                xlsSheetConfig.xlsCellConfigs().add(xlsCellConfig);
            }
        }

        return startCellIndex;
    }

    /**
     * 表头合并
     * @param sheet
     */
    private static void mergeSameCells(Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        for (int rowIndex = 0; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            int lastCellNum = row.getLastCellNum();
            for (int cellIndex = 0; cellIndex <= lastCellNum; cellIndex++) {
                Cell cell = row.getCell(cellIndex);
                if (cell == null) continue;
                if (isMergedCell(sheet, cell)) continue;
                String cellValue = cell.getStringCellValue();

                int startRow = rowIndex;
                int endRow = rowIndex;
                int startCell = cellIndex;
                int endCell = cellIndex;

                //System.out.println(String.format("=====%d:%d - %d:%d", startRow,startCell,endRow,endCell));
                for (int c = cellIndex + 1; c <= lastCellNum; c++) {
                    Row nextRow = sheet.getRow(rowIndex);
                    if (nextRow == null) break;
                    Cell nextCell = nextRow.getCell(c);
                    if (nextCell == null) break;

                    if (cellValue.equals(nextCell.getStringCellValue())) {
                        endCell = c;
                    } else {
                        break;
                    }
                }

                // 列查找
                for (int c = cellIndex + 1; c <= lastCellNum; c++) {
                    for (int r = rowIndex + 1; r <= lastRowNum; r++) {
                        Row innerNextRow = sheet.getRow(r);
                        if (innerNextRow == null) break;
                        Cell innerNextCell = innerNextRow.getCell(cellIndex);
                        if (innerNextCell == null) break;

                        if (cellValue.equals(innerNextCell.getStringCellValue())) {
                            endRow = r;
                        } else {
                            break;
                        }
                    }
                }
                // System.out.println(String.format("%d:%d - %d:%d", startRow,startCell,endRow,endCell));
                // 如果找到相同的单元格则进行合并
                if (endRow > startRow || endCell > startCell) {
                    sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, startCell, endCell));
                    cellIndex = endCell;
                }
            }
        }
    }

    // 判断单元格是否已合并
    private static boolean isMergedCell(Sheet sheet, Cell cell) {
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();

        for (CellRangeAddress range : mergedRegions) {
            if (range.isInRange(rowIndex, columnIndex)) {
                return true;
            }
        }
        return false;
    }


}
