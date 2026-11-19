package com.kiet.studentskill.service;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String id) {
        super("Student not found with ID: " + id);
    }
}
