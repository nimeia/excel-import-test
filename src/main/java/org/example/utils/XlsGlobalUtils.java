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

    public static Map<Class<?>, XlsExcelConfig> allExcelConfigs = new HashMap<>();

//    public static Map<Class<?>, List<XlsSheetConfig>> allSheetConfigs = new HashMap<>();

//    public static Map<Class<?>, List<XlsCellConfig>> allCellConfigs = new HashMap<>();

    public static Map<Class<?>, byte[]> allTemplateCaches = new HashMap<>();

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
                XlsAnnotationUtils.setFieldValue(xlsSheetConfig,"field", null);
                XlsAnnotationUtils.setFieldValue(xlsSheetConfig,"setMethod", null);
                XlsAnnotationUtils.setFieldValue(xlsSheetConfig,"getMethod", null);
                initSheets(clazz,xlsSheetConfig);
            } else {
                List<Field> allFields = new ArrayList<>();
                allFields.addAll(ReflectionUtils.getAllFields(clazz, (f) -> true));
                Collections.sort(allFields, (e1, e2) -> XlsAnnotationUtils.getFieldValueForJdk12(e1, "slot", Integer.class) - XlsAnnotationUtils.getFieldValueForJdk12(e2, "slot", Integer.class));
                for (Field field : allFields) {
                    if(field.isAnnotationPresent(XlsIgnore.class)) continue;
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
                            XlsAnnotationUtils.setFieldValue(xlsSheetConfig,"field", field);
                            XlsAnnotationUtils.setFieldValue(xlsSheetConfig,"setMethod", XlsAnnotationUtils.getSetterMethod(field.getType(),field.getName()));
                            XlsAnnotationUtils.setFieldValue(xlsSheetConfig,"getMethod", XlsAnnotationUtils.getSetterMethod(field.getType(),field.getName()));
                            xlsSheets.add(xlsSheetConfig);
                            initSheets(actualTypeArgument,xlsSheetConfig);
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
                                XlsAnnotationUtils.setFieldValue(xlsSheetConfig,"field", field);
                                XlsAnnotationUtils.setFieldValue(xlsSheetConfig,"setMethod", XlsAnnotationUtils.getSetterMethod(field.getType(),field.getName()));
                                XlsAnnotationUtils.setFieldValue(xlsSheetConfig,"getMethod", XlsAnnotationUtils.getSetterMethod(field.getType(),field.getName()));
                                xlsSheets.add(xlsSheetConfig);
                                initSheets(field.getType(),xlsSheetConfig);
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
                        if(!xlsCellConfig.getBindField().equals(fileName)){
                            throw new RuntimeException("模板配置字段顺序不一致，请检查！"+fileName);
                        }

                        //XlsCellConfig xlsCell = xlsCells.stream().filter(e -> e.getBindField().equals(fileName)).findFirst().orElse(null);
                        // Field e = allCellFields.get(xlsCell);
//                        Field e = xlsCell.getField();
//                        fieldList.add(e);
//                        xlsCellList.add(xlsCell);
                    }

                    for (int rowIndex = xlsSheet.getHeadRow(); rowIndex <= lastRowNum; rowIndex++) {
                        Row currentRowData = dataSheet.getRow(rowIndex);
                        int lastCellNum = currentRowData.getLastCellNum();
                        Object data = xlsSheet.getBindClass().getDeclaredConstructor().newInstance();
                        for (int i = 0; i < lastCellNum; i++) {
                            //Field field = fieldList.get(i);
                            Field field = xlsCellList.get(i).getField();
                            Method setMethod = xlsCellList.get(i).getSetMethod();
                            // System.out.println(String.format("get cell %s,%s for %s", rowIndex, i, field));
                            // dataMap.put(headFiledNames.get(i), currentRowData.getCell(i).getStringCellValue());
                            Object cellValue = ExcelUtil.getCellValue(currentRowData.getCell(i));
                            if (cellValue != null) {
                                Class<?> aClass = xlsCellList.get(i).getCellType();
                                if (cellValue.getClass() == aClass) {
                                    if(setMethod!=null){
                                        setMethod.invoke(data,cellValue);
                                    }else{
                                        field.set(data, cellValue);
                                    }
                                } else if (cellValue instanceof String) {
                                    if(setMethod!=null){
                                        setMethod.invoke(data,StringToTypeConverterUtils.convert(cellValue.toString(), aClass));
                                    }else{
                                        field.set(data, StringToTypeConverterUtils.convert(cellValue.toString(), aClass));
                                    }
                                } else {
                                    if(setMethod!=null){
                                        setMethod.invoke(data,  StringToTypeConverterUtils.convert(cellValue.toString(), aClass));
                                    }else{
                                        field.set(data,  StringToTypeConverterUtils.convert(cellValue.toString(), aClass));
                                    }
                                }
                            }
                        }
                        datalist.add(data);
                    }
                    if (sameFlag) {
                        return datalist;
                    }
                    // Field field = allSheetFields.get(xlsSheet);
                    Field field = xlsSheet.getField();
                    Method setMethod = xlsSheet.getSetMethod();
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        if(setMethod!=null){
                            setMethod.invoke(result,datalist);
                        }else{
                            field.set(result, datalist);
                        }
                    } else {
                        if (datalist != null && datalist.size() > 0) {
                            if(setMethod!=null){
                                setMethod.invoke(result,datalist.get(0));
                            }else{
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
     * 动态设置注解内容，module java 需要配置 JAVA_TOOL_OPTIONS: --add-opens=java.base/sun.reflect.annotation=ALL-UNNAMED
     *
     * @param aClass
     */
    private static void initSheets(Class<?> aClass,XlsSheetConfig xlsSheetConfig) {
        List<Field> allFields = new ArrayList<>();
        allFields.addAll(ReflectionUtils.getAllFields(aClass, f -> true));
        Collections.sort(allFields, (e1, e2) -> XlsAnnotationUtils.getFieldValueForJdk12(e1, "slot", Integer.class) - XlsAnnotationUtils.getFieldValueForJdk12(e2, "slot", Integer.class));
        allFields.forEach(field -> {
            if (field.isAnnotationPresent(XlsCell.class) && !field.isAnnotationPresent(XlsIgnore.class)) {
                XlsCell xlsCell = field.getAnnotation(XlsCell.class);
                XlsCellConfig xlsCellConfig = new XlsCellConfig(xlsCell);
                XlsAnnotationUtils.setFieldValue(xlsCellConfig, "bindClass", aClass);
                XlsAnnotationUtils.setFieldValue(xlsCellConfig, "bindField", field.getName());
                // XlsAnnotationUtils.setAnnotationValue(xlsCell,"bindClass", aClass);
                // XlsAnnotationUtils.setAnnotationValue(xlsCell,"bindField", field.getName());
                if (xlsCell.headTitle().length == 0) {
                    //XlsAnnotationUtils.setAnnotationValue(xlsCell,"headTitle", new String[]{field.getName()});
                    XlsAnnotationUtils.setFieldValue(xlsCellConfig, "headTitle", new String[]{field.getName()});
                }
                if (xlsCell.cellType() == void.class) {
                    //XlsAnnotationUtils.setAnnotationValue(xlsCell,"cellType", field.getType());
                    XlsAnnotationUtils.setFieldValue(xlsCellConfig, "cellType", field.getType());
                }
                List<XlsCellConfig> xlsCells = xlsSheetConfig.getXlsCellConfigs();//allCellConfigs.computeIfAbsent(aClass, k -> new ArrayList<>());
                boolean hadAdd = xlsCells.stream().anyMatch(e -> e.equals(xlsCellConfig));
                if (!hadAdd) {
                    xlsCells.add(xlsCellConfig);
                    field.setAccessible(true);
                    //allCellFields.put(xlsCellConfig, field);
                    XlsAnnotationUtils.setFieldValue(xlsCellConfig, "field", field);
                    XlsAnnotationUtils.setFieldValue(xlsCellConfig, "setMethod", XlsAnnotationUtils.getSetterMethod(field.getType(),field.getName()));
                    XlsAnnotationUtils.setFieldValue(xlsCellConfig, "getMethod", XlsAnnotationUtils.getGetterMethod(field.getType(),field.getName()));
                }
            }
        });
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
