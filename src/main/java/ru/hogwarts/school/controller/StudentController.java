package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.util.FacultyDTOMapper;
import ru.hogwarts.school.util.StudentDTOMapper;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final StudentDTOMapper studentDTOMapper;
    private final FacultyDTOMapper facultyDTOMapper;

    public StudentController(StudentService studentService, StudentDTOMapper studentDTOMapper, FacultyDTOMapper facultyDTOMapper) {
        this.studentService = studentService;
        this.studentDTOMapper = studentDTOMapper;
        this.facultyDTOMapper = facultyDTOMapper;
    }

    @GetMapping("{id}")
    public ResponseEntity<StudentDtoOut> getStudentInfo(@PathVariable Long id) {
        Student student = studentService.getStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(studentDTOMapper.studentToDtoOut(student));
    }

    @GetMapping
    public ResponseEntity<List<StudentDtoOut>> getAllStudents() {
        List<StudentDtoOut> allStudentsDto = studentService.getAllStudents()
                .stream().map(studentDTOMapper::studentToDtoOut).toList();
        return ResponseEntity.ok(allStudentsDto);
    }

    @GetMapping("/faculty/{id}")
    public ResponseEntity<FacultyDtoOut> getStudentsFaculty(@PathVariable Long id) {
        Student student = studentService.getStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(facultyDTOMapper.facultyToDtoOut(student.getFaculty()));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<StudentDtoOut>> getStudentsByAgeBetween
            (@RequestParam(required = false) int minAge, @RequestParam(required = false) int maxAge) {
        List<StudentDtoOut> studentsDtoByAgeBetween = studentService.getStudentsByAgeBetween(minAge, maxAge)
                .stream().map(studentDTOMapper::studentToDtoOut).toList();
        return ResponseEntity.ok(studentsDtoByAgeBetween);
    }

    @GetMapping("/filter/{age}")
    public ResponseEntity<List<StudentDtoOut>> getStudentsByAge(@PathVariable int age) {
        List<StudentDtoOut> studentsDtoByAge = studentService.filterStudentsByAge(age)
                .stream().map(studentDTOMapper::studentToDtoOut).toList();
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
    public ResponseEntity<List<StudentDtoOut>> getLatestFiveStudents() {
        List<StudentDtoOut> latestFiveStudents = studentService.getLatestFiveOfStudents()
                .stream().map(studentDTOMapper::studentToDtoOut).toList();
        return ResponseEntity.ok(latestFiveStudents);
    }

    @PostMapping
    public ResponseEntity<StudentDtoOut> addStudent(@RequestBody StudentDtoIn studentDtoIn) {
        Student student = studentService.addStudent(studentDTOMapper.dtoInToStudent(studentDtoIn));
        return ResponseEntity.ok(studentDTOMapper.studentToDtoOut(student));
    }

    @PutMapping
    public ResponseEntity<StudentDtoOut> editStudent(@RequestBody StudentDtoOut studentDtoOut) {
        Student foundStudent = studentService.getStudent(studentDtoOut.getId());
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Student editedStudent = studentService.editStudent(studentDTOMapper.dtoOutToStudent(studentDtoOut));
        return ResponseEntity.ok(studentDTOMapper.studentToDtoOut(editedStudent));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<StudentDtoOut> deleteStudent(@PathVariable long id) {
        Student foundStudent = studentService.getStudent(id);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        studentService.removeStudent(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<StudentDtoOut> deleteAllStudents() {
        studentService.clearAll();
        return ResponseEntity.ok().build();
    }
}
