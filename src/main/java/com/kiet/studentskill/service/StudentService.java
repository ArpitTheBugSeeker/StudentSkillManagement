package com.kiet.studentskill.service;

import com.kiet.studentskill.entity.Student;
import com.kiet.studentskill.repository.StudentRepository;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        if (student.getStudentId() == null || student.getStudentId().isBlank()) {
            throw new IllegalArgumentException("studentId is required");
        }
        if (studentRepository.existsById(student.getStudentId())) {
            throw new IllegalArgumentException("Student ID already exists: " + student.getStudentId());
        }
        validateScores(student.getProgrammingScore(), student.getMathematicsScore());
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() { return studentRepository.findAll(); }

    @Transactional(readOnly = true)
    public Optional<Student> findById(String id) { return studentRepository.findById(id); }

    @Transactional(readOnly = true)
    public List<Student> search(String email, String course) {
        if (email != null && !email.isBlank() && course != null && !course.isBlank()) {
            Map<String, Student> matches = new LinkedHashMap<>();
            studentRepository.findByEmailIgnoreCase(email).forEach(student -> matches.put(student.getStudentId(), student));
            studentRepository.findByCourseNameIgnoreCase(course).forEach(student -> matches.put(student.getStudentId(), student));
            return List.copyOf(matches.values());
        }
        if (email != null && !email.isBlank()) return studentRepository.findByEmailIgnoreCase(email);
        if (course != null && !course.isBlank()) return studentRepository.findByCourseNameIgnoreCase(course);
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Student> meritList() {
        return studentRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(Student::getAverageScore).reversed()).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, List<String>> sharedTechnicalSkills() {
        Map<String, List<String>> studentsBySkill = new LinkedHashMap<>();
        studentRepository.findAll().forEach(student -> {
            if (student.getTechnicalSkills() == null) return;
            Set<String> distinctSkills = student.getTechnicalSkills().stream()
                    .filter(skill -> skill != null && !skill.isBlank()).collect(Collectors.toSet());
            distinctSkills.forEach(skill -> studentsBySkill.computeIfAbsent(skill, ignored -> new java.util.ArrayList<>())
                    .add(student.getStudentName()));
        });
        return studentsBySkill.entrySet().stream().filter(entry -> entry.getValue().size() >= 2)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (first, second) -> first, LinkedHashMap::new));
    }

    @Transactional(readOnly = true)
    public List<Student> weakProgrammingStrongMath() {
        return studentRepository.findAll().stream()
                .filter(student -> student.getProgrammingScore() < 50 && student.getMathematicsScore() >= 75).toList();
    }

    public Student updateStudent(String id, double programmingScore, double mathematicsScore, List<String> skills) {
        validateScores(programmingScore, mathematicsScore);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        student.setProgrammingScore(programmingScore);
        student.setMathematicsScore(mathematicsScore);
        student.setTechnicalSkills(skills);
        return studentRepository.save(student);
    }

    public void deleteStudent(String id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.delete(student);
    }

    private void validateScores(double programmingScore, double mathematicsScore) {
        if (!Double.isFinite(programmingScore) || programmingScore <= 0 || programmingScore >= 100
                || !Double.isFinite(mathematicsScore) || mathematicsScore <= 0 || mathematicsScore >= 100) {
            throw new IllegalArgumentException("Programming and mathematics scores must be strictly between 0 and 100.");
        }
    }
}
