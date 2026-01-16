package app.model;

import java.io.Serializable; 

public class Student implements Serializable {
    public String student_no;
    public String name;
    public String department;

    public Student(String student_no, String name, String department) {
        this.student_no = student_no;
        this.name = name;
        this.department = department;
    }
}