package org.example.test.vo;

import org.example.vo.XlsExcel;
import org.example.vo.XlsIndex;

import java.util.List;

@XlsExcel(title = "导入模板", category = {"type|key|name", "type1|key1|name1", "type2|key2|name2"})
public class MainVo  {
    @XlsIndex(index = 1)
    private List<Student> studentList;

    @XlsIndex(index = 0)
    private List<Teacher> teachers;

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    public List<Student> getSheet2List() {
        return studentList;
    }

    public void setSheet2List(List<Student> studentList) {
        this.studentList = studentList;
    }

}
