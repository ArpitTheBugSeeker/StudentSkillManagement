package com.kiet.studentskill.repository;

import com.kiet.studentskill.entity.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByEmailIgnoreCase(String email);
    List<Student> findByCourseNameIgnoreCase(String courseName);
}
