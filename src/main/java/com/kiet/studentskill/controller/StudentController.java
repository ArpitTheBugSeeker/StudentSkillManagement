package com.kiet.studentskill.controller;

import com.kiet.studentskill.entity.Student;
import com.kiet.studentskill.service.StudentNotFoundException;
import com.kiet.studentskill.service.StudentService;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) { this.studentService = studentService; }

    @PostMapping
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        Student saved = studentService.addStudent(student);
        return ResponseEntity.created(URI.create("/students/" + saved.getStudentId())).body(saved);
    }

    @GetMapping
    public List<Student> getAllStudents() { return studentService.getAllStudents(); }

    @GetMapping("/search")
    public List<Student> search(@RequestParam(required = false) String email,
                                @RequestParam(required = false) String course) {
        return studentService.search(email, course);
    }

    @GetMapping("/merit-list")
    public List<Student> meritList() { return studentService.meritList(); }

    @GetMapping("/same-skills")
    public Map<String, List<String>> sameSkills() { return studentService.sharedTechnicalSkills(); }

    @GetMapping("/weak-prog-strong-math")
    public List<Student> weakProgrammingStrongMath() { return studentService.weakProgrammingStrongMath(); }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable String id) {
        return studentService.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable String id, @RequestBody Student update) {
        return studentService.updateStudent(id, update.getProgrammingScore(), update.getMathematicsScore(), update.getTechnicalSkills());
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return Map.of("message", "Student " + id + " deleted successfully.");
    }
}

@RestControllerAdvice
class StudentApiExceptionHandler {
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(StudentNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }
}
