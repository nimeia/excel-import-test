package org.example.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.vo.XlsCell;
import org.example.vo.XlsExcel;
import org.example.vo.XlsIgnore;
import org.example.vo.XlsSheet;
import org.example.xls.config.XlsCellConfig;
import org.example.xls.config.XlsExcelConfig;
import org.example.xls.config.XlsSheetConfig;
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

//    public static Map<Class<?>, List<XlsSheetConfig>> allSheetConfigs = new HashMap<>();

//    public static Map<Class<?>, List<XlsCellConfig>> allCellConfigs = new HashMap<>();

    //public static Map<XlsCellConfig, Field> allCellFields = new HashMap<>();

    //public static Map<XlsSheetConfig, Field> allSheetFields = new HashMap<>();

    /**
     * 初始化excel导入配置
     *
     * @param basePackages
     */
    public static void init(String[] basePackages) {
        Map<String, Boolean> dubCheckMap = new HashMap<>();

        // init category data
        List<Class<?>> allXlsExcel = XlsAnnotationUtils.getAllClassWithAnnotiaon(basePackages, XlsExcel.class);
        for (Class<?> clazz : allXlsExcel) {
            XlsExcel xlsExcel = clazz.getAnnotation(XlsExcel.class);
            XlsExcelConfig xlsExcelConfig = new XlsExcelConfig(xlsExcel);
            XlsAnnotationUtils.setFieldValue(xlsExcelConfig, "bindClass", clazz);
            //XlsAnnotationUtils.setAnnotationValue(xlsExcel,"bindClass", clazz);
            allExcelConfigs.put(clazz, xlsExcelConfig);
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
            List<XlsSheetConfig> xlsSheets = xlsExcelConfig.getSheetConfigs();//allSheetConfigs.computeIfAbsent(clazz, k -> new ArrayList<>());
            if (clazz.isAnnotationPresent(XlsSheet.class)) {
                XlsSheet xlsSheet = clazz.getAnnotation(XlsSheet.class);
                XlsSheetConfig xlsSheetConfig = new XlsSheetConfig(xlsSheet);
                XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "bindClass", clazz);
                XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "ownerClass", clazz);
                //XlsAnnotationUtils.setAnnotationValue(xlsSheet,"bindClass", clazz);
                //XlsAnnotationUtils.setAnnotationValue(xlsSheet, "bindClass", clazz);
                //XlsAnnotationUtils.setAnnotationValue(xlsSheet, "ownerClass", clazz);

                xlsSheets.add(xlsSheetConfig);
                //allSheetFields.put(xlsSheetConfig, null);
                XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "field", null);
                XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "setMethod", null);
                XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "getMethod", null);
                initSheets(clazz, xlsSheetConfig, null, 0, null);
            } else {
                List<Field> allFields = new ArrayList<>();
                allFields.addAll(ReflectionUtils.getAllFields(clazz, (f) -> true));
                Collections.sort(allFields, (e1, e2) -> XlsAnnotationUtils.getFieldValueForJdk12(e1, "slot", Integer.class) - XlsAnnotationUtils.getFieldValueForJdk12(e2, "slot", Integer.class));
                for (Field field : allFields) {
                    if (field.isAnnotationPresent(XlsIgnore.class)) continue;
                    Class<?> type = field.getType();
                    XlsSheet xlsSheet = null;
                    if (Collection.class.isAssignableFrom(type)) {
                        // 当前集合的泛型类型
                        Type genericType = field.getGenericType();
                        ParameterizedType pt = (ParameterizedType) genericType;
                        // 得到泛型里的class类型对象
                        Class<?> actualTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
                        xlsSheet = actualTypeArgument.getAnnotation(XlsSheet.class);
                    } else {
                        xlsSheet = field.getType().getAnnotation(XlsSheet.class);
                    }

                    if (xlsSheet != null) {
                        if (Collection.class.isAssignableFrom(type)) {
                            // 当前集合的泛型类型
                            Type genericType = field.getGenericType();
                            ParameterizedType pt = (ParameterizedType) genericType;
                            // 得到泛型里的class类型对象
                            Class<?> actualTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
                            XlsSheetConfig xlsSheetConfig = new XlsSheetConfig(xlsSheet);
                           /* XlsAnnotationUtils.setAnnotationValue(xlsSheet, "isCollection", true);
                            XlsAnnotationUtils.setAnnotationValue(xlsSheet, "bindClass", actualTypeArgument);
                            XlsAnnotationUtils.setAnnotationValue(xlsSheet, "bindField", field.getName());
                            XlsAnnotationUtils.setAnnotationValue(xlsSheet, "ownerClass", clazz);*/
                            XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "isCollection", true);
                            XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "bindClass", actualTypeArgument);
                            XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "bindField", field.getName());
                            XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "ownerClass", clazz);

                            field.setAccessible(true);
                            //allSheetFields.put(xlsSheetConfig, field);
                            XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "field", field);
                            XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "setMethod", XlsAnnotationUtils.getSetterMethod(field.getType(), field.getName()));
                            XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "getMethod", XlsAnnotationUtils.getSetterMethod(field.getType(), field.getName()));
                            xlsSheets.add(xlsSheetConfig);
                            initSheets(actualTypeArgument, xlsSheetConfig, null, 0, null);
                        } else {
                            if (!Map.class.isAssignableFrom(field.getType())) {
                                XlsSheetConfig xlsSheetConfig = new XlsSheetConfig(xlsSheet);

                                XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "bindClass", field.getType());
                                XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "bindField", field.getName());
                                XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "ownerClass", clazz);
                                /*XlsAnnotationUtils.setAnnotationValue(xlsSheet, "bindClass", field.getType());
                                XlsAnnotationUtils.setAnnotationValue(xlsSheet, "bindField", field.getName());
                                XlsAnnotationUtils.setAnnotationValue(xlsSheet, "ownerClass", clazz);*/
                                field.setAccessible(true);
                                //allSheetFields.put(xlsSheetConfig, field);
                                XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "field", field);
                                XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "setMethod", XlsAnnotationUtils.getSetterMethod(field.getType(), field.getName()));
                                XlsAnnotationUtils.setFieldValue(xlsSheetConfig, "getMethod", XlsAnnotationUtils.getSetterMethod(field.getType(), field.getName()));
                                xlsSheets.add(xlsSheetConfig);
                                initSheets(field.getType(), xlsSheetConfig, null, 0, null);
                            } else {
                                throw new RuntimeException("未支持集合类型：" + field.getType().getName());
                            }
                        }
                    }
                }
            }

            //xlsSheets 排序
            for (int i = 0; i < xlsSheets.size(); i++) {
                int finalI = i;
                boolean hadIndexed = xlsSheets.stream().anyMatch(e -> e.getIndex() == finalI);
                if (!hadIndexed) {
                    xlsSheets.stream()
                            .filter(e -> e.getIndex() == -1)
                            .findFirst()
                            .ifPresent(e -> {
                                //XlsAnnotationUtils.setAnnotationValue(e,"index", finalI);
                                XlsAnnotationUtils.setFieldValue(e, "index", finalI);
                            });
                }
            }
            Collections.sort(xlsSheets, (e1, e2) -> e1.getIndex() - e2.getIndex());

            for (XlsSheetConfig xlsSheet : xlsSheets) {
                int headRow = 1;
                //xlsCells 排序
                List<XlsCellConfig> xlsCells = xlsSheet.getXlsCellConfigs();//allCellConfigs.get(xlsSheet.getBindClass());
                for (int i = 0; i < xlsCells.size(); i++) {
                    int finalI = i;
                    boolean hadIndexed = xlsCells.stream().anyMatch(e -> e.getIndex() == finalI);
                    if (!hadIndexed) {
                        xlsCells.stream()
                                .filter(e -> e.getIndex() == -1)
                                .findFirst()
                                .ifPresent(e -> {
//                                    Field field = allCellFields.get(e);
//                                    allCellFields.remove(e);
                                    //XlsAnnotationUtils.setAnnotationValue(e, "index", finalI);
                                    XlsAnnotationUtils.setFieldValue(e, "index", finalI);
//                                    allCellFields.put(e, field);
                                });
                    }
                    if (xlsCells.get(i).getHeadTitle().length > headRow) {
                        headRow = xlsCells.get(i).getHeadTitle().length;
                    }
                }
//                Field field = allSheetFields.get(xlsSheet);
//                allSheetFields.remove(xlsSheet);
                //XlsAnnotationUtils.setAnnotationValue(xlsSheet,"headRow", headRow);
                XlsAnnotationUtils.setFieldValue(xlsSheet, "headRow", headRow);
//                allSheetFields.put(xlsSheet, field);
                Collections.sort(xlsCells, (e1, e2) -> e1.getIndex() - e2.getIndex());
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

            List<XlsSheetConfig> xlsSheetList = xlsExcel.getSheetConfigs();//allSheetConfigs.get(clazz);
            if (xlsSheetList == null) return null;

            for (XlsSheetConfig xlsSheet : xlsSheetList) {
                Sheet dataSheet = workbook.createSheet(xlsSheet.getTitle());
                int sheetIndex = workbook.getSheetIndex(dataSheet);
                if (xlsSheet.isSheetActive()) {
                    workbook.setActiveSheet(sheetIndex);
                }
                if (xlsSheet.isHidden()) {
                    workbook.setSheetHidden(sheetIndex, true);
                }

                //cell
                List<XlsCellConfig> xlsCells = xlsSheet.getXlsCellConfigs();//allCellConfigs.get(xlsSheet.getBindClass());

                for (int j = 0; j < xlsSheet.getHeadRow(); j++) {
                    Row row = dataSheet.createRow(j);
                    for (int i = 0; i < xlsCells.size(); i++) {
                        Cell cell = row.createCell(i);
                        XlsCellConfig xlsCell = xlsCells.get(i);
                        int index = (xlsSheet.getHeadRow() - xlsCell.getHeadTitle().length) - j >= 0 ? 0 : j - (xlsSheet.getHeadRow() - xlsCell.getHeadTitle().length);
//                        System.out.println(xlsSheet.headRow()+","+xlsCell.headTitle().length+","+j+","+i+","+index);
                        String value =/*xlsCell.bindField()+"-"+xlsCell.index()+"-"+*/xlsCell.getHeadTitle()[index];
//                        System.out.println(i+":"+j+"-"+value);
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
                            Comment comment = drawing.createCellComment(anchor);
                            RichTextString str = factory.createRichTextString(xlsCell.getBindField());
                            comment.setString(str);
                            comment.setAuthor("X");
                            comment.setVisible(false);

                            // 设置批注到单元格
                            cell.setCellComment(comment);
                        }

                        if (!xlsCell.getStyleMethod().equals("")) {
                            // 创建单元格样式
                            try {
                                Method method = xlsCell.getBindClass().getMethod(xlsCell.getStyleMethod(), Sheet.class);
                                CellStyle cellStyle = (CellStyle) method.invoke(xlsCell.getBindClass().getDeclaredConstructor().newInstance(), dataSheet);
                                // 应用样式到单元格
                                cell.setCellStyle(cellStyle);
                            } catch (NoSuchMethodException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (InstantiationException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        //设置列格式
                        if (!xlsCell.getColumnStyleMethod().equals("")) {
                            // 创建单元格样式
                            try {
                                Method method = xlsCell.getBindClass().getMethod(xlsCell.getStyleMethod(), Sheet.class);
                                CellStyle cellStyle = (CellStyle) method.invoke(xlsCell.getBindClass().getDeclaredConstructor().newInstance(), dataSheet);
                                // 应用样式到单元格
                                //cell.setCellStyle(cellStyle);
                                dataSheet.setDefaultColumnStyle(cell.getColumnIndex(), cellStyle);
                            } catch (NoSuchMethodException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (InstantiationException e) {
                                throw new RuntimeException(e);
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
                List<XlsSheetConfig> xlsSheetList = xlsExcel.getSheetConfigs();//allSheetConfigs.get(clazz);
                if (xlsSheetList == null) return null;

                Object result = xlsExcel.getBindClass().getDeclaredConstructor().newInstance();

                boolean sameFlag = false;
                if (xlsExcel.getBindClass().isAnnotationPresent(XlsSheet.class)) {
                    sameFlag = true;
                }

                for (XlsSheetConfig xlsSheet : xlsSheetList) {
                    Sheet dataSheet = workbook.getSheet(xlsSheet.getTitle());
//                    List<XlsCellConfig> xlsCells = xlsSheet.getXlsCellConfigs();//allCellConfigs.get(xlsSheet.getBindClass());
                    int lastRowNum = dataSheet.getLastRowNum();
                    List datalist = new ArrayList<>();

                    //header 处理
                    Row row = dataSheet.getRow(xlsSheet.getHeadRow() - 1);
                    int headLastCellNum = row.getLastCellNum();
//                    List<Field> fieldList = new ArrayList<>();
                    List<XlsCellConfig> xlsCellList = xlsSheet.getXlsCellConfigs();//new ArrayList<>();
                    //检查是否有乱序
                    for (int i = 0; i < headLastCellNum; i++) {
                        String fileName = row.getCell(i).getCellComment().getString().getString();
                        XlsCellConfig xlsCellConfig = xlsCellList.get(i);
                        if (!xlsCellConfig.getBindField().equals(fileName)) {
                            throw new RuntimeException("模板配置字段顺序不一致，请检查！" + fileName);
                        }

                        // XlsCellConfig xlsCell = xlsCells.stream().filter(e -> e.getBindField().equals(fileName)).findFirst().orElse(null);
                        // Field e = allCellFields.get(xlsCell);
//                        Field e = xlsCell.getField();
//                        fieldList.add(e);
//                        xlsCellList.add(xlsCell);
                    }

                    for (int rowIndex = xlsSheet.getHeadRow(); rowIndex <= lastRowNum; rowIndex++) {
                        Row currentRowData = dataSheet.getRow(rowIndex);
                        int lastCellNum = currentRowData.getLastCellNum();
                        Object mainSheetData = xlsSheet.getBindClass().getDeclaredConstructor().newInstance();
                        for (int i = 0; i < lastCellNum; i++) {
                            //Field field = fieldList.get(i);
                            XlsCellConfig xlsCellConfig = xlsCellList.get(i);

                            Object data = mainSheetData;
                            if (xlsCellConfig.getInnerSheetToClass() != void.class) {
                                //主表属性是 collection 还是单对象
                                if (Collection.class.isAssignableFrom(xlsCellConfig.getField().getType())) {
                                    Object co = xlsCellConfig.getField().get(mainSheetData);
                                    if (co == null) {
                                        //o is list
                                        Object list = xlsCellConfig.getField().getType().getDeclaredConstructor().newInstance();
                                        xlsCellConfig.getField().set(mainSheetData, list);
                                        co = list;
                                    }
                                    // 计算当前是第几个
                                    if (xlsCellConfig.getInnerSheetIndex() > ((Collection<?>) co).size()) {
                                        data = xlsCellConfig.getInnerSheetToClass().getDeclaredConstructor().newInstance();
                                        ((Collection) co).add(data);
                                    } else {
                                        data = ((Collection<?>) co).toArray()[xlsCellConfig.getInnerSheetIndex()];
                                    }
                                    // ((Collection)co).add(data);
                                } else {
                                    Object co = xlsCellConfig.getField().get(mainSheetData);
                                    if (co == null) {
                                        Object o = xlsCellConfig.getInnerSheetToClass().getDeclaredConstructor().newInstance();
                                        co = o;
                                        xlsCellConfig.getField().set(mainSheetData, co);
                                    }
                                    data = co;
                                }
                            }
                            Field field = xlsCellConfig.getField();
                            Method setMethod = xlsCellConfig.getSetMethod();
                            // System.out.println(String.format("get cell %s,%s for %s", rowIndex, i, field));
                            // dataMap.put(headFiledNames.get(i), currentRowData.getCell(i).getStringCellValue());
                            Object cellValue = ExcelUtil.getCellValue(currentRowData.getCell(i));
                            if (cellValue != null) {
                                Class<?> aClass = xlsCellConfig.getCellType();
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
                    Field field = xlsSheet.getField();
                    Method setMethod = xlsSheet.getSetMethod();
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
        List<XlsSheetConfig> sheetConfigs = xlsExcel.getSheetConfigs();
        List<Map<XlsSheetConfig, Object>> result = new ArrayList<>();
        if (Collection.class.isAssignableFrom(o.getClass())) {
            //这种场景只能是单表，
            return doTransform(xlsExcel.getSheetConfigs().get(0), o);
        } else {
//            Set<Field> allFields = ReflectionUtils.getAllFields(clazz,
//                    (f) -> f.getType().isAnnotationPresent(XlsSheet.class) && !f.getType().isAnnotationPresent(XlsIgnore.class));
//            allFields.forEach(field -> {
//
//            });
            sheetConfigs.forEach(sheetConfig -> {
                Object target = null;
                if (sheetConfig.getGetMethod() != null) {
                    try {
                        target = sheetConfig.getGetMethod().invoke(o);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        target = sheetConfig.getField().get(o);
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
            if (sheetConfig.getToClass() == null) {
                return Map.of(sheetConfig, target);
            }
            List result = new ArrayList<>();

            int loopTime = (target instanceof Collection<?> ? ((Collection<?>) target).size() : 1);
            boolean collectionFlag = (target instanceof Collection<?>);
            List<?> listObj = collectionFlag ? Arrays.asList(((Collection<?>) target).toArray()) : new ArrayList<>();
            Class<?> targetClass = sheetConfig.getToClass();
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
                sheetConfig.getXlsCellConfigs().forEach(cellConfig -> {
                    if (cellConfig.getTargetSetMethod() != null) {
                        try {
                            Object resultO = null;
                            if (cellConfig.getGetMethod() != null) {
                                resultO = cellConfig.getGetMethod().invoke(finalLoopObj);
                            } else {
                                resultO = cellConfig.getField().get(finalLoopObj);
                            }

                            cellConfig.getTargetSetMethod().invoke(targetObj, resultO);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else if (cellConfig.getTargetField() != null) {
                        try {
                            Object resultO = null;
                            if (cellConfig.getGetMethod() != null) {
                                resultO = cellConfig.getGetMethod().invoke(finalLoopObj);
                            } else {
                                resultO = cellConfig.getField().get(finalLoopObj);
                            }
                            cellConfig.getTargetField().set(targetObj, resultO);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
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
     * 动态设置注解内容，module java 需要配置 JAVA_TOOL_OPTIONS: --add-opens=java.base/sun.reflect.annotation=ALL-UNNAMED
     *
     * @param bindClass
     * @param xlsSheetConfig
     * @param innerSheetToClass
     * @param startIndex
     * @param innerShellGroupIndex
     * @return
     */
    private static int initSheets(Class<?> bindClass, XlsSheetConfig xlsSheetConfig, Class<?> innerSheetToClass, int startIndex, Integer innerShellGroupIndex) {
        Class<?> toClass = null;
        if (innerSheetToClass == null) {
            toClass = xlsSheetConfig.getToClass();
        } else {
            toClass = innerSheetToClass;
        }
        Class<?> finalToClass = toClass;

        List<Field> allTargetFields = ReflectionUtils.getAllFields(toClass, f -> {
            f.setAccessible(true);
            return true;
        }).stream().toList();
        List<Method> allTargetSetterMethods = ReflectionUtils.getAllMethods(toClass, f -> f.getName().startsWith("set")).stream().toList();
        List<Method> allTargetGetterMethods = ReflectionUtils.getAllMethods(toClass, f -> f.getName().startsWith("get")).stream().toList();

        List<Field> allFields = new ArrayList<>(ReflectionUtils.getAllFields(bindClass, f -> f.isAnnotationPresent(XlsCell.class)));
        //按类定义排序
        Collections.sort(allFields, (e1, e2) -> XlsAnnotationUtils.getFieldValueForJdk12(e1, "slot", Integer.class) - XlsAnnotationUtils.getFieldValueForJdk12(e2, "slot", Integer.class));
        //按类的定义重排

        Collections.sort(allFields, (e1, e2) -> e1.getAnnotation(XlsCell.class).index() - e2.getAnnotation(XlsCell.class).index());

        for (Field field : allFields) {
            startIndex++;
            if (field.isAnnotationPresent(XlsCell.class) && !field.isAnnotationPresent(XlsIgnore.class)) {
                XlsCell xlsCell = field.getAnnotation(XlsCell.class);
                XlsCellConfig xlsCellConfig = new XlsCellConfig(xlsCell);
                if (innerShellGroupIndex != null) {
                    XlsAnnotationUtils.setFieldValue(xlsCellConfig, "innerSheetIndex", innerShellGroupIndex);
                    XlsAnnotationUtils.setFieldValue(xlsCellConfig, "innerSheetToClass", bindClass);
                }
                XlsAnnotationUtils.setFieldValue(xlsCellConfig, "index", startIndex);
                if (innerShellGroupIndex == null && (xlsCellConfig.getInnerSheetToClass() != void.class
                        || (xlsCellConfig.getInnerSheetToClass() == void.class && !CommonTypeCheckerUtils.isCommonType(field.getType())))) {
                    Class<?> innerSheetBindClass = xlsCellConfig.getInnerSheetToClass();
                    if (innerSheetBindClass == void.class) {
                        if (Collection.class.isAssignableFrom(field.getType())) {
                            // 当前集合的泛型类型
                            Type genericType = field.getGenericType();
                            ParameterizedType pt = (ParameterizedType) genericType;
                            // 得到泛型里的class类型对象
                            innerSheetBindClass = (Class<?>) pt.getActualTypeArguments()[0];
                        } else {
                            innerSheetBindClass = field.getType();
                            XlsAnnotationUtils.setFieldValue(xlsCellConfig, "innerSheetRowCount", 1);
                        }
                        //XlsAnnotationUtils.setFieldValue(xlsCellConfig, "innerSheetToClass", innerSheetBindClass);
                    }
                    for (int j = 0; j < xlsCellConfig.getInnerSheetRowCount(); j++) {
                        // startIndex = initSheets(innerSheetBindClass, xlsSheetConfig, xlsCellConfig.getInnerSheetToClass(), startIndex, j);
                        List<Field> innerAllFields = new ArrayList<>(ReflectionUtils.getAllFields(innerSheetBindClass, f -> {
                            f.setAccessible(true);
                            return f.isAnnotationPresent(XlsCell.class);
                        }));

                        List<Field> innerAllTargetFields = ReflectionUtils.getAllFields(xlsCell.innerSheetToClass(), f -> {
                            f.setAccessible(true);
                            return true;
                        }).stream().toList();
                        List<Method> innerAllTargetSetterMethods = ReflectionUtils.getAllMethods(xlsCell.innerSheetToClass(), f -> f.getName().startsWith("set")).stream().toList();
                        List<Method> innerAllTargetGetterMethods = ReflectionUtils.getAllMethods(xlsCell.innerSheetToClass(), f -> f.getName().startsWith("get")).stream().toList();

                        List<Method> innerAllSetterMethods = ReflectionUtils.getAllMethods(innerSheetBindClass, f -> f.getName().startsWith("set")).stream().toList();
                        List<Method> innerAllGetterMethods = ReflectionUtils.getAllMethods(innerSheetBindClass, f -> f.getName().startsWith("get")).stream().toList();

                        for (int k = 0; k < innerAllFields.size(); k++) {
                            Field innerField = innerAllFields.get(k);
                            XlsCell innerXlsCellAnnotation = innerField.getAnnotation(XlsCell.class);
                            XlsCellConfig innerXlsCellConfig = new XlsCellConfig(innerXlsCellAnnotation);
                            List<XlsCellConfig> xlsCells = xlsSheetConfig.getXlsCellConfigs();
                            xlsCells.add(innerXlsCellConfig);
                            XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "bindClass", bindClass);
                            XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "field", field);
                            XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "bindField", field.getName());
                            if (innerXlsCellAnnotation.headTitle().length == 0) {
                                XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "headTitle", new String[]{innerField.getName()});
                            }
                            if (innerXlsCellAnnotation.cellType() == void.class) {
                                XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "cellType", field.getType());
                            }
                            XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "setMethod", XlsAnnotationUtils.getSetterMethod(innerField.getType(), innerField.getName()));
                            XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "getMethod", XlsAnnotationUtils.getGetterMethod(innerField.getType(), innerField.getName()));

                            XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "innerSheetToClass", innerSheetBindClass);
                            XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "innerSheetField", innerField);
                            XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "innerSheetSetMethod"
                                    , innerAllSetterMethods.stream().filter(e -> e.getName().toLowerCase().endsWith(innerField.getName())).findFirst().orElse(null));
                            XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "innerSheetGetMethod"
                                    , innerAllGetterMethods.stream().filter(e -> e.getName().toLowerCase().endsWith(innerField.getName())).findFirst().orElse(null));
                            XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "innerSheetTargetField"
                                    , innerAllTargetFields.stream().filter(e -> e.getName().equals(innerXlsCellAnnotation.innerSheetToField())).findFirst().orElse(null));
                            XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "innerSheetTargetSetMethod"
                                    , innerAllTargetSetterMethods.stream().filter(e -> e.getName().toLowerCase().endsWith(innerXlsCellAnnotation.innerSheetToField())).findFirst().orElse(null));
                            XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "innerSheetTargetGetMethod"
                                    , innerAllTargetGetterMethods.stream().filter(e -> e.getName().toLowerCase().endsWith(innerXlsCellAnnotation.innerSheetToField())).findFirst().orElse(null));
                            XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "innerSheetIndex", j);
                            XlsAnnotationUtils.setFieldValue(innerXlsCellConfig, "index", ++startIndex);
                        }
                    }
                } else {
                    XlsAnnotationUtils.setFieldValue(xlsCellConfig, "bindClass", bindClass);
                    XlsAnnotationUtils.setFieldValue(xlsCellConfig, "bindField", field.getName());
                    // XlsAnnotationUtils.setAnnotationValue(xlsCell,"bindClass", bindClass);
                    // XlsAnnotationUtils.setAnnotationValue(xlsCell,"bindField", field.getName());
                    if (xlsCell.headTitle().length == 0) {
                        //XlsAnnotationUtils.setAnnotationValue(xlsCell,"headTitle", new String[]{field.getName()});
                        XlsAnnotationUtils.setFieldValue(xlsCellConfig, "headTitle", new String[]{field.getName()});
                    }
                    if (xlsCell.cellType() == void.class) {
                        //XlsAnnotationUtils.setAnnotationValue(xlsCell,"cellType", field.getType());
                        XlsAnnotationUtils.setFieldValue(xlsCellConfig, "cellType", field.getType());
                    }
                    List<XlsCellConfig> xlsCells = xlsSheetConfig.getXlsCellConfigs();
                    //allCellConfigs.computeIfAbsent(bindClass, k -> new ArrayList<>());
                    xlsCells.add(xlsCellConfig);
                    field.setAccessible(true);
                    //allCellFields.put(xlsCellConfig, field);
                    XlsAnnotationUtils.setFieldValue(xlsCellConfig, "field", field);
                    XlsAnnotationUtils.setFieldValue(xlsCellConfig, "setMethod", XlsAnnotationUtils.getSetterMethod(field.getType(), field.getName()));
                    XlsAnnotationUtils.setFieldValue(xlsCellConfig, "getMethod", XlsAnnotationUtils.getGetterMethod(field.getType(), field.getName()));

                    boolean flag = false;
                    if (xlsCellConfig.getToField() != null && !"".equals(xlsCellConfig.getToField().trim())) {
                        Field targetField = null;
                        Method targetSetterMethod = null;
                        Method targetGetterMethod = null;

                        if (finalToClass != void.class) {
                            targetField = allTargetFields.stream().filter(e -> e.getName().equals(xlsCellConfig.getToField())).findFirst().orElse(null);
                            targetSetterMethod = allTargetSetterMethods
                                    .stream()
                                    .filter(e -> e.getName().toLowerCase().endsWith(xlsCellConfig.getToField()))
                                    .findFirst().orElse(null);
                            targetGetterMethod = allTargetGetterMethods
                                    .stream()
                                    .filter(e -> e.getName().toLowerCase().endsWith(xlsCellConfig.getToField()))
                                    .findFirst().orElse(null);
                        } else {
                            //使用同名方法设置
                            targetField = allTargetFields.stream().filter(e -> e.getName().equals(xlsCellConfig.getToField())).findFirst().orElse(null);
                            if (xlsCellConfig.getSetMethod() != null)
                                targetSetterMethod = allTargetSetterMethods.stream().filter(e -> e.getName().equals(xlsCellConfig.getSetMethod().getName())).findFirst().orElse(null);
                            if (xlsCellConfig.getGetMethod() != null)
                                targetGetterMethod = allTargetGetterMethods.stream().filter(e -> e.getName().equals(xlsCellConfig.getGetMethod().getName())).findFirst().orElse(null);
                        }
                        XlsAnnotationUtils.setFieldValue(xlsCellConfig, "targetField", targetField);
                        XlsAnnotationUtils.setFieldValue(xlsCellConfig, "targetSetMethod", targetSetterMethod);
                        XlsAnnotationUtils.setFieldValue(xlsCellConfig, "targetGetMethod", targetGetterMethod);

                        flag = true;
                    }

                    if (!flag && xlsSheetConfig.isFillByFiledName()) {
                        Field targetField = allTargetFields.stream().filter(e -> e.getName().equals(field.getName())).findFirst().orElse(null);
                        XlsAnnotationUtils.setFieldValue(xlsCellConfig, "targetField", targetField);
                        if (xlsCellConfig.getSetMethod() != null) {
                            Method targetSetterMethod = allTargetSetterMethods.stream().filter(e -> e.getName().equals(xlsCellConfig.getSetMethod().getName())).findFirst().orElse(null);
                            XlsAnnotationUtils.setFieldValue(xlsCellConfig, "targetSetMethod", targetSetterMethod);
                        }
                        if (xlsCellConfig.getGetMethod() != null) {
                            Method targetGetterMethod = allTargetGetterMethods.stream().filter(e -> e.getName().equals(xlsCellConfig.getGetMethod().getName())).findFirst().orElse(null);
                            XlsAnnotationUtils.setFieldValue(xlsCellConfig, "targetGetMethod", targetGetterMethod);
                        }
                    }
                }
            }
        }
        return startIndex;
    }


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
