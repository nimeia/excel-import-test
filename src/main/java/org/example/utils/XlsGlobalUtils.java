package org.example.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.vo.XlsExcel;
import org.example.vo.XlsCell;
import org.example.vo.XlsSheet;
import org.reflections.ReflectionUtils;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class XlsGlobalUtils {

    public static final String EXCEL_BASE_INFO_SHEET = "base-info";
    /**
     * 格式：<br/>
     * <b>type1 </b>: [{key:value},{kye:value}] <br/>
     * <b>type2</b> : [{key:value},{kye:value}] <br/>
     *
     */
    public static Map<String, List<Map<String,String>>> allCategories = new HashMap<String, List<Map<String,String>>>();

    public static Map<Class<?>,XlsExcel> allExcelConfigs = new HashMap<>();

    public static Map<Class<?>,List<XlsSheet>> allSheetConfigs = new HashMap<>();

    public static Map<Class<?>,List<XlsCell>> allCellConfigs = new HashMap<>();

    public static Map<Class<?>,byte []> allTemplateCaches = new HashMap<>();

    public static Map<XlsCell,Field> allCellFields = new HashMap<>();

    public static Map<XlsSheet,Field> allSheetFields = new HashMap<>();

    /**
     * 初始化excel导入配置
     * @param basePackages
     */
    public static void init(String [] basePackages) {

        Map<String,Boolean> dubCheckMap = new HashMap<>();

        // init category data
        List<Class<?>> allXlsExcel = XlsAnnotationUtils.getAllClassWithAnnotiaon(basePackages, XlsExcel.class);
        for(Class<?> clazz : allXlsExcel){
            XlsExcel xlsExcel = clazz.getAnnotation(XlsExcel.class);
            XlsAnnotationUtils.setAnnotationValue(xlsExcel,"bindClass", clazz);
            allExcelConfigs.put( clazz, xlsExcel);
            String[] categoryDefs = xlsExcel.category();
            String fullCategoryName = String.join(",", categoryDefs);
            if(dubCheckMap.containsKey(fullCategoryName)){
                throw new RuntimeException("重复的配置类型: "+ fullCategoryName);
            }else{
                dubCheckMap.put(fullCategoryName, true);
            }
            for (String category : categoryDefs) {
                String[] split = category.split("\\|");
                String type = split[0];
                List<Map<String, String>> categoryList = allCategories.computeIfAbsent(type, k -> new ArrayList<>());
                Map<String, String> newCategory = Map.of(split[1], split[2]);
                if(!categoryList.contains(newCategory)){
                    categoryList.add(newCategory);
                }
            }

            //init sheet data
            List<XlsSheet> xlsSheets = allSheetConfigs.computeIfAbsent(clazz, k -> new ArrayList<>());
            if (clazz.isAnnotationPresent(XlsSheet.class)) {
                XlsSheet xlsSheet = clazz.getAnnotation(XlsSheet.class);
                XlsAnnotationUtils.setAnnotationValue(xlsSheet,"bindClass", clazz);
                XlsAnnotationUtils.setAnnotationValue(xlsSheet, "bindClass", clazz);
                XlsAnnotationUtils.setAnnotationValue(xlsSheet, "ownerClass", clazz);
                xlsSheets.add(xlsSheet);
                allSheetFields.put(xlsSheet, null);
                initSheets(clazz);
            } else {
                List<Field> allFields = new ArrayList<>();
                allFields.addAll(ReflectionUtils.getAllFields(clazz, (f)->true));
                Collections.sort(allFields,(e1,e2) -> XlsAnnotationUtils.getFieldValueForJdk12(e1,"slot", Integer.class) - XlsAnnotationUtils.getFieldValueForJdk12(e2,"slot",Integer.class) );
                for (Field field : allFields) {
                    Class<?> type = field.getType();
                    XlsSheet xlsSheet = null;
                    if (Collection.class.isAssignableFrom(type)) {
                        // 当前集合的泛型类型
                        Type genericType = field.getGenericType();
                        ParameterizedType pt = (ParameterizedType) genericType;
                        // 得到泛型里的class类型对象
                        Class<?> actualTypeArgument = (Class<?>)pt.getActualTypeArguments()[0];
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
                            Class<?> actualTypeArgument = (Class<?>)pt.getActualTypeArguments()[0];
                            XlsAnnotationUtils.setAnnotationValue(xlsSheet, "isCollection", true);
                            XlsAnnotationUtils.setAnnotationValue(xlsSheet, "bindClass", actualTypeArgument);
                            XlsAnnotationUtils.setAnnotationValue(xlsSheet, "bindField", field.getName());
                            XlsAnnotationUtils.setAnnotationValue(xlsSheet, "ownerClass", clazz);
                            field.setAccessible(true);
                            allSheetFields.put(xlsSheet, field);
                            xlsSheets.add(xlsSheet);
                            initSheets(actualTypeArgument);
                        } else {
                            if (!Map.class.isAssignableFrom(field.getType())) {
                                XlsAnnotationUtils.setAnnotationValue(xlsSheet, "bindClass", field.getType());
                                XlsAnnotationUtils.setAnnotationValue(xlsSheet, "bindField", field.getName());
                                XlsAnnotationUtils.setAnnotationValue(xlsSheet, "ownerClass", clazz);
                                field.setAccessible(true);
                                allSheetFields.put(xlsSheet, field);
                                xlsSheets.add(xlsSheet);
                                initSheets(field.getType());
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
                boolean hadIndexed = xlsSheets.stream().anyMatch(e -> e.index() == finalI);
                if(!hadIndexed){
                    xlsSheets.stream()
                            .filter(e -> e.index() == -1)
                            .findFirst()
                            .ifPresent(e -> {XlsAnnotationUtils.setAnnotationValue(e,"index", finalI);});
                }
            }
            Collections.sort(xlsSheets,(e1,e2)->e1.index()-e2.index());

            for (XlsSheet xlsSheet : xlsSheets) {
                int headRow = 1;
                //xlsCells 排序
                List<XlsCell> xlsCells = allCellConfigs.get(xlsSheet.bindClass());
                for (int i = 0; i < xlsCells.size(); i++) {
                    int finalI = i;
                    boolean hadIndexed = xlsCells.stream().anyMatch(e -> e.index() == finalI);
                    if (!hadIndexed) {
                        xlsCells.stream()
                                .filter(e -> e.index() == -1)
                                .findFirst()
                                .ifPresent(e -> {
                                    Field field = allCellFields.get(e);
                                    allCellFields.remove(e);
                                    XlsAnnotationUtils.setAnnotationValue(e, "index", finalI);
                                    allCellFields.put(e,field);
                                });
                    }
                    if (xlsCells.get(i).headTitle().length > headRow) {
                        headRow = xlsCells.get(i).headTitle().length;
                    }
                }
                Field field = allSheetFields.get(xlsSheet);
                allSheetFields.remove(xlsSheet);
                XlsAnnotationUtils.setAnnotationValue(xlsSheet,"headRow", headRow);
                allSheetFields.put(xlsSheet,field);
                Collections.sort(xlsCells,(e1,e2)->e1.index()-e2.index());
            }
        }
    }

    /**
     * 返回模板缓存
     * @param clazz
     * @return
     */
    public static byte[] getXlsTemplate(Class<?> clazz){
        return allTemplateCaches.computeIfAbsent(clazz ,k -> {

            Workbook workbook = new XSSFWorkbook();

            XlsExcel xlsExcel = allExcelConfigs.get(clazz);
            if(xlsExcel == null) return null;

            List<XlsSheet> xlsSheetList = allSheetConfigs.get(clazz);
            if(xlsSheetList == null) return null;

            for (XlsSheet xlsSheet : xlsSheetList) {
                Sheet dataSheet = workbook.createSheet(xlsSheet.title());
                int sheetIndex = workbook.getSheetIndex(dataSheet);
                if(xlsSheet.sheetActive()){
                    workbook.setActiveSheet(sheetIndex);
                }
                if(xlsSheet.hidden()){
                    workbook.setSheetHidden(sheetIndex, true);
                }

                //cell
                List<XlsCell> xlsCells = allCellConfigs.get(xlsSheet.bindClass());

                for(int j = 0; j < xlsSheet.headRow(); j++){
                    Row row = dataSheet.createRow(j);
                    for (int i = 0; i < xlsCells.size(); i++) {
                        Cell cell = row.createCell(i);
                        XlsCell xlsCell = xlsCells.get(i);
                        int index = (xlsSheet.headRow() - xlsCell.headTitle().length) - j >= 0 ? 0 : j - (xlsSheet.headRow() - xlsCell.headTitle().length);
//                        System.out.println(xlsSheet.headRow()+","+xlsCell.headTitle().length+","+j+","+i+","+index);
                        String value =/*xlsCell.bindField()+"-"+xlsCell.index()+"-"+*/xlsCell.headTitle()[ index ];
//                        System.out.println(i+":"+j+"-"+value);
                        cell.setCellValue(value );

                        if(cell.getCellComment() == null)
                        {
                            // 创建绘图对象
                            Drawing<?> drawing = dataSheet.createDrawingPatriarch();

                            // 创建批注锚点
                            CreationHelper factory = workbook.getCreationHelper();
                            ClientAnchor anchor = factory.createClientAnchor();
                            anchor.setCol1(cell.getColumnIndex()); // 批注开始的列
                            anchor.setRow1(cell.getRowIndex()); // 批注开始的行
                            anchor.setCol2(cell.getColumnIndex()+2);
                            anchor.setRow2(cell.getRowIndex()+2);

                            // 创建批注对象
                            Comment comment = drawing.createCellComment(anchor);
                            RichTextString str = factory.createRichTextString(xlsCell.bindField());
                            comment.setString(str);
                            comment.setAuthor("X");
                            comment.setVisible(false);

                            // 设置批注到单元格
                            cell.setCellComment(comment);
                        }

                        if(!xlsCell.styleMethod().equals("")){
                            // 创建单元格样式
                            try {
                                Method method = xlsCell.bindClass().getMethod(xlsCell.styleMethod(),Sheet.class);
                                CellStyle cellStyle = (CellStyle)method.invoke(xlsCell.bindClass().getDeclaredConstructor().newInstance(),dataSheet);
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
                        if (!xlsCell.columnStyleMethod().equals("")) {
                            // 创建单元格样式
                            try {
                                Method method = xlsCell.bindClass().getMethod(xlsCell.styleMethod(),Sheet.class);
                                CellStyle cellStyle = (CellStyle)method.invoke(xlsCell.bindClass().getDeclaredConstructor().newInstance(),dataSheet);
                                // 应用样式到单元格
                                //cell.setCellStyle(cellStyle);
                                dataSheet.setDefaultColumnStyle(cell.getColumnIndex(),cellStyle);
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
                XlsExcel xlsExcel = allExcelConfigs.get(clazz);
                if (xlsExcel == null) return null;
                List<XlsSheet> xlsSheetList = allSheetConfigs.get(clazz);
                if (xlsSheetList == null) return null;

                Object result = xlsExcel.bindClass().getDeclaredConstructor().newInstance();

                boolean sameFlag = false;
                if (xlsExcel.bindClass().isAnnotationPresent(XlsSheet.class)) {
                    sameFlag = true;
                }

                for (XlsSheet xlsSheet : xlsSheetList) {
                    Sheet dataSheet = workbook.getSheet(xlsSheet.title());
                    List<XlsCell> xlsCells = allCellConfigs.get(xlsSheet.bindClass());
                    int lastRowNum = dataSheet.getLastRowNum();
                    List datalist = new ArrayList<>();

                    //header 处理
                    Row row = dataSheet.getRow(xlsSheet.headRow() - 1);
                    int headLastCellNum = row.getLastCellNum();
                    List<Field> fieldList = new ArrayList<>();
                    List<XlsCell> xlsCellList = new ArrayList<>();
                    for (int i = 0; i < headLastCellNum; i++) {
                        String fileName = row.getCell(i).getCellComment().getString().getString();
                        XlsCell xlsCell = xlsCells.stream().filter(e -> e.bindField().equals(fileName)).findFirst().orElse(null);
                        Field e = allCellFields.get(xlsCell);
                        fieldList.add(e);
                        xlsCellList.add(xlsCell);
                    }

                    for (int rowIndex = xlsSheet.headRow(); rowIndex <=lastRowNum; rowIndex++) {
                        Row currentRowData = dataSheet.getRow(rowIndex);
                        int lastCellNum = currentRowData.getLastCellNum();
                        Object data = xlsSheet.bindClass().getDeclaredConstructor().newInstance();
                        for (int i = 0; i < lastCellNum; i++) {
                            Field field = fieldList.get(i);
                            System.out.println(String.format("get cell %s,%s for %s", rowIndex,i, field));
                            //dataMap.put(headFiledNames.get(i), currentRowData.getCell(i).getStringCellValue());
                            Object cellValue = ExcelUtil.getCellValue(currentRowData.getCell(i));
                            if(cellValue != null){
                                Class<?> aClass = xlsCellList.get(i).cellType();
                                if(cellValue.getClass() ==  aClass){
                                    field.set(data, cellValue);
                                }else if(cellValue instanceof String){
                                    field.set(data, StringToTypeConverterUtils.convert(cellValue.toString(), aClass));
                                }else{
                                    field.set(data, StringToTypeConverterUtils.convert(cellValue.toString(), aClass));
                                }
                            }
                        }
                        datalist.add(data);
                    }
                    if (sameFlag) {
                        return datalist;
                    }
                    Field field = allSheetFields.get(xlsSheet);
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        field.set(result, datalist);
                    } else {
                        if (datalist != null && datalist.size() > 0) {
                            field.set(result, datalist.get(0));
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
     * @param aClass
     */
    private static void initSheets(Class<?> aClass) {
        List<Field> allFields = new ArrayList<>();
        allFields.addAll(ReflectionUtils.getAllFields(aClass, f -> true));
        Collections.sort(allFields,(e1,e2) -> XlsAnnotationUtils.getFieldValueForJdk12(e1,"slot", Integer.class) - XlsAnnotationUtils.getFieldValueForJdk12(e2,"slot",Integer.class) );
        allFields.forEach(field -> {
            if(field.isAnnotationPresent(XlsCell.class)){
                XlsCell xlsCell = field.getAnnotation(XlsCell.class);
                XlsAnnotationUtils.setAnnotationValue(xlsCell,"bindClass", aClass);
                XlsAnnotationUtils.setAnnotationValue(xlsCell,"bindField", field.getName());
                if(xlsCell.headTitle().length == 0){
                    XlsAnnotationUtils.setAnnotationValue(xlsCell,"headTitle", new String[]{field.getName()});
                }
                if(xlsCell.cellType() == void.class){
                    XlsAnnotationUtils.setAnnotationValue(xlsCell,"cellType", field.getType());
                }
                List<XlsCell> xlsCells = allCellConfigs.computeIfAbsent(aClass, k -> new ArrayList<>());
                boolean hadAdd = xlsCells.stream().anyMatch(e -> e == xlsCell);
                if(!hadAdd){
                    xlsCells.add(xlsCell);
                    field.setAccessible(true);
                    allCellFields.put(xlsCell, field);
                }
            }
        });
    }


    private static void mergeSameCells(Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        boolean canMerge = false;
        for (int rowIndex = 0; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            int lastCellNum = row.getLastCellNum();
            for (int cellIndex = 0; cellIndex <= lastCellNum; cellIndex++) {
                if(cellIndex ==0 && rowIndex ==0){
                    canMerge = true;
                }

                Cell cell = row.getCell(cellIndex);
                if (cell == null) continue;
                if(isMergedCell(sheet,cell)) continue;
                String cellValue = cell.getStringCellValue();

                int startRow = rowIndex;
                int endRow = rowIndex;
                int startCell = cellIndex;
                int endCell = cellIndex;

//                System.out.println(String.format("=====%d:%d - %d:%d", startRow,startCell,endRow,endCell));

                for (int c = cellIndex+1 ; c <= lastCellNum; c++) {
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
                for (int c = cellIndex+1 ; c <= lastCellNum; c++) {
                    for (int r = rowIndex + 1; r <= lastRowNum; r++) {
                        Row innerNextRow = sheet.getRow(r);
                        if (innerNextRow == null) break;
                        Cell innerNextCell = innerNextRow.getCell(cellIndex);
                        if (innerNextCell == null) break;

                        if (cellValue.equals(innerNextCell.getStringCellValue())) {
                            endRow = r ;
                        }else {
                            break;
                        }
                    }
                }
//                System.out.println(String.format("%d:%d - %d:%d", startRow,startCell,endRow,endCell));
                // 如果找到相同的单元格则进行合并
                if(endRow>startRow ||endCell>startCell){
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
