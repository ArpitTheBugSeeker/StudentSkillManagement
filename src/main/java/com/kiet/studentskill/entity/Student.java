package com.kiet.studentskill.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "students")
public class Student {
    @Id
    private String studentId;
    private String studentName;
    private String email;
    private String courseName;
    private double programmingScore;
    private double mathematicsScore;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> technicalSkills = new ArrayList<>();

    public Student() { }

    public Student(String studentId, String studentName, String email, String courseName,
                   double programmingScore, double mathematicsScore, List<String> technicalSkills) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.courseName = courseName;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        this.technicalSkills = technicalSkills == null ? new ArrayList<>() : new ArrayList<>(technicalSkills);
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public double getProgrammingScore() { return programmingScore; }
    public void setProgrammingScore(double programmingScore) { this.programmingScore = programmingScore; }
    public double getMathematicsScore() { return mathematicsScore; }
    public void setMathematicsScore(double mathematicsScore) { this.mathematicsScore = mathematicsScore; }
    public List<String> getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = technicalSkills == null ? new ArrayList<>() : new ArrayList<>(technicalSkills);
    }

    @Transient
    public double getAverageScore() { return (programmingScore + mathematicsScore) / 2.0; }

    @Transient
    public String getPerformanceCategory() {
        double average = getAverageScore();
        if (average >= 85) return "Excellent";
        if (average >= 70) return "Good";
        if (average >= 50) return "Satisfactory";
        return "Needs Improvement";
    }

    @Transient
    public String getStrongerSubject() {
        return programmingScore > mathematicsScore ? "Programming"
                : mathematicsScore > programmingScore ? "Mathematics" : "Equal";
    }

    @Override
    public String toString() {
        return "Student{" + "studentId='" + studentId + '\'' + ", studentName='" + studentName + '\''
                + ", email='" + email + '\'' + ", courseName='" + courseName + '\''
                + ", programmingScore=" + programmingScore + ", mathematicsScore=" + mathematicsScore
                + ", technicalSkills=" + technicalSkills + '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Student student)) return false;
        return Double.compare(student.programmingScore, programmingScore) == 0
                && Double.compare(student.mathematicsScore, mathematicsScore) == 0
                && Objects.equals(studentId, student.studentId) && Objects.equals(studentName, student.studentName)
                && Objects.equals(email, student.email) && Objects.equals(courseName, student.courseName)
                && Objects.equals(technicalSkills, student.technicalSkills);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, studentName, email, courseName, programmingScore, mathematicsScore, technicalSkills);
    }
}
