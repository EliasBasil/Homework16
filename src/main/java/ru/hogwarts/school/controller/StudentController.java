package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDtoResponse;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoResponse;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.util.FacultyDTOMapper;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final FacultyDTOMapper facultyDTOMapper;

    public StudentController(StudentService studentService, FacultyDTOMapper facultyDTOMapper) {
        this.studentService = studentService;
        this.facultyDTOMapper = facultyDTOMapper;
    }

    @GetMapping("{id}")
    public ResponseEntity<StudentDtoResponse> getStudentInfo(@PathVariable Long id) {
        StudentDtoResponse studentDtoResponse = studentService.getStudentDtoOut(id);
        if (studentDtoResponse == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(studentDtoResponse);
    }

    @GetMapping
    public ResponseEntity<List<StudentDtoResponse>> getAllStudents() {
        List<StudentDtoResponse> allStudentsDto = studentService.getAllStudents();
        return ResponseEntity.ok(allStudentsDto);
    }

    @GetMapping("/faculty/{id}")
    public ResponseEntity<FacultyDtoResponse> getStudentsFaculty(@PathVariable Long id) {
        Student student = studentService.getStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(facultyDTOMapper.facultyToDtoOut(student.getFaculty()));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<StudentDtoResponse>> getStudentsByAgeBetween
            (@RequestParam(required = false) int minAge, @RequestParam(required = false) int maxAge) {
        List<StudentDtoResponse> studentsDtoByAgeBetween = studentService.getStudentsByAgeBetween(minAge, maxAge);
        return ResponseEntity.ok(studentsDtoByAgeBetween);
    }

    @GetMapping("/filter/{age}")
    public ResponseEntity<List<StudentDtoResponse>> getStudentsByAge(@PathVariable int age) {
        List<StudentDtoResponse> studentsDtoByAge = studentService.filterStudentsByAge(age);
        return ResponseEntity.ok(studentsDtoByAge);
    }

    @GetMapping("/total_number")
    public ResponseEntity<Integer> getTotalNumberOfStudents() {
        Integer total = studentService.getTotalNumberOfStudents();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/average_age")
    public ResponseEntity<Double> getAverageAgeOfStudents() {
        Double averageAgeOfStudents = studentService.getAverageAgeOfStudents();
        return ResponseEntity.ok(averageAgeOfStudents);
    }

    @GetMapping("/latest_five")
    public ResponseEntity<List<StudentDtoResponse>> getLatestFiveStudents() {
        List<StudentDtoResponse> latestFiveStudents = studentService.getLatestFiveOfStudents();
        return ResponseEntity.ok(latestFiveStudents);
    }

    @PostMapping
    public ResponseEntity<StudentDtoResponse> addStudent(@RequestBody StudentDtoIn studentDtoIn) {
        StudentDtoResponse studentDtoResponse = studentService.addStudent(studentDtoIn);
        return ResponseEntity.ok(studentDtoResponse);
    }

    @PutMapping
    public ResponseEntity<StudentDtoResponse> editStudent(@RequestBody StudentDtoResponse studentDtoResponse) {
        Student foundStudent = studentService.getStudent(studentDtoResponse.getId());
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        StudentDtoResponse editedStudent = studentService.editStudent(studentDtoResponse);
        return ResponseEntity.ok(editedStudent);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<StudentDtoResponse> deleteStudent(@PathVariable long id) {
        Student foundStudent = studentService.getStudent(id);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        studentService.removeStudent(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<StudentDtoResponse> deleteAllStudents() {
        studentService.clearAll();
        return ResponseEntity.ok().build();
    }
}
