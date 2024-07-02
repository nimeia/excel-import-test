package org.example.xls.config;

import org.example.vo.XlsSheet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class XlsSheetConfig {
    /**
     * 真实对象的类型,例如 List<User》 ,bindclass 为user
     */
    private Class<?> fieldRealTypeClass;
    /**
     * 绑定属性名
     */
    private Field field;

    /**
     * set method
     */
    private Method setMethod;
    /**
     * get method
     */
    private Method getMethod;

    /**
     * 主类类型
     */
    private Class<?> ownerClass;

    /**
     * 主类中是否以为集合类
     */
    private boolean isCollection;
    /**
     * 正文标题
     */
    private String title;
    /**
     * 是否激活
     */
    private boolean sheetActive;
    /**
     * 标题行数
     */
    private int headRow;
    /**
     * 填充目标对象
     */
    private Class<?> toClass;

    /**
     * excel 中显示位置
     *
     * @return
     */
    private int index;

    /**
     * 是否隐藏
     *
     * @return
     */
    private boolean hidden;

    /**
     * 指向父对象
     * @return
     */
    private Class<?> parentClass;

    private Field parentContainerField;
    private Method parentContainerSetMethod;
    private Method parentContainerGetMethod;

    /**
     * 父级关联ID
     * @return
     */
    private Field parentLinkId;
    private Method parentLinkSetMethod;
    private Method parentLinkGetMethod;

    /**
     * 关联ID
     * @return
     */
    private Field linkId;
    private Method linkSetMethod;
    private Method linkGetMethod;

    private List<XlsCellConfig> xlsCellConfigs = new ArrayList<XlsCellConfig>();

    public XlsSheetConfig(XlsSheet xlsSheet) {
        this.fieldRealTypeClass = xlsSheet.getClass();
        this.ownerClass = xlsSheet.getClass();
        this.title = xlsSheet.title();
        this.sheetActive = xlsSheet.sheetActive();
        this.headRow = xlsSheet.headRow();
        this.index = xlsSheet.index();
        this.hidden = xlsSheet.hidden();
        this.toClass = xlsSheet.toClass();
        this.parentClass = xlsSheet.parentClass();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XlsSheetConfig that = (XlsSheetConfig) o;
        return Objects.equals(fieldRealTypeClass, that.fieldRealTypeClass) && Objects.equals(ownerClass, that.ownerClass) && Objects.equals(field.getName(), that.field.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldRealTypeClass, ownerClass, field.getName());
    }


    public Class<?> fieldRealTypeClass() {
        return fieldRealTypeClass;
    }

    public XlsSheetConfig fieldRealTypeClass(Class<?> bindClass) {
        this.fieldRealTypeClass = bindClass;
        return this;
    }

    public Class<?> ownerClass() {
        return ownerClass;
    }

    public XlsSheetConfig ownerClass(Class<?> ownerClass) {
        this.ownerClass = ownerClass;
        return this;
    }

    public Field field() {
        return field;
    }

    public XlsSheetConfig field(Field field) {
        this.field = field;
        return this;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public XlsSheetConfig isCollection(boolean collection) {
        isCollection = collection;
        return this;
    }

    public String title() {
        return title;
    }

    public XlsSheetConfig title(String title) {
        this.title = title;
        return this;
    }

    public boolean sheetActive() {
        return sheetActive;
    }

    public XlsSheetConfig sheetActive(boolean sheetActive) {
        this.sheetActive = sheetActive;
        return this;
    }

    public int headRow() {
        return headRow;
    }

    public XlsSheetConfig headRow(int headRow) {
        this.headRow = headRow;
        return this;
    }

    public Class<?> toClass() {
        return toClass;
    }

    public XlsSheetConfig toClass(Class<?> toClass) {
        this.toClass = toClass;
        return this;
    }

    public Method setMethod() {
        return setMethod;
    }

    public XlsSheetConfig setMethod(Method setMethod) {
        this.setMethod = setMethod;
        return this;
    }

    public Method getMethod() {
        return getMethod;
    }

    public XlsSheetConfig getMethod(Method getMethod) {
        this.getMethod = getMethod;
        return this;
    }

    public int index() {
        return index;
    }

    public XlsSheetConfig index(int index) {
        this.index = index;
        return this;
    }

    public boolean hidden() {
        return hidden;
    }

    public XlsSheetConfig hidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public List<XlsCellConfig> xlsCellConfigs() {
        return xlsCellConfigs;
    }

    public XlsSheetConfig xlsCellConfigs(List<XlsCellConfig> xlsCellConfigs) {
        this.xlsCellConfigs = xlsCellConfigs;
        return this;
    }

    public Class<?> parentClass() {
        return parentClass;
    }

    public XlsSheetConfig parentClass(Class<?> parentClass) {
        this.parentClass = parentClass;
        return this;
    }

    public Field parentContainerField() {
        return parentContainerField;
    }

    public XlsSheetConfig parentContainerField(Field parentContainerField) {
        this.parentContainerField = parentContainerField;
        return this;
    }

    public Method parentContainerSetMethod() {
        return parentContainerSetMethod;
    }

    public XlsSheetConfig parentContainerSetMethod(Method parentContainerSetMethod) {
        this.parentContainerSetMethod = parentContainerSetMethod;
        return this;
    }

    public Method parentContainerGetMethod() {
        return parentContainerGetMethod;
    }

    public XlsSheetConfig parentContainerGetMethod(Method parentContainerGetMethod) {
        this.parentContainerGetMethod = parentContainerGetMethod;
        return this;
    }

    public Field parentLinkId() {
        return parentLinkId;
    }

    public XlsSheetConfig parentLinkId(Field parentLinkId) {
        this.parentLinkId = parentLinkId;
        return this;
    }

    public Method parentLinkSetMethod() {
        return parentLinkSetMethod;
    }

    public XlsSheetConfig parentLinkSetMethod(Method parentLinkSetMethod) {
        this.parentLinkSetMethod = parentLinkSetMethod;
        return this;
    }

    public Method parentLinkGetMethod() {
        return parentLinkGetMethod;
    }

    public XlsSheetConfig parentLinkGetMethod(Method parentLinkGetMethod) {
        this.parentLinkGetMethod = parentLinkGetMethod;
        return this;
    }

    public Field linkId() {
        return linkId;
    }

    public XlsSheetConfig linkId(Field linkId) {
        this.linkId = linkId;
        return this;
    }

    public Method linkSetMethod() {
        return linkSetMethod;
    }

    public XlsSheetConfig linkSetMethod(Method linkSetMethod) {
        this.linkSetMethod = linkSetMethod;
        return this;
    }

    public Method linkGetMethod() {
        return linkGetMethod;
    }

    public XlsSheetConfig linkGetMethod(Method linkGetMethod) {
        this.linkGetMethod = linkGetMethod;
        return this;
    }
}
