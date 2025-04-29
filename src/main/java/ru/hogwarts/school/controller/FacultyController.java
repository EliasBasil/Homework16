package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.dto.FacultyDtoInWithId;
import ru.hogwarts.school.dto.FacultyDtoResponse;
import ru.hogwarts.school.dto.StudentDtoResponse;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;
    private final StudentService studentService;

    public FacultyController(FacultyService facultyService, StudentService studentService) {
        this.facultyService = facultyService;
        this.studentService = studentService;
    }

    @GetMapping("{id}")
    public ResponseEntity<FacultyDtoResponse> getFacultyInfo(@PathVariable Long id) {
        FacultyDtoResponse facultyDtoResponse = facultyService.getFacultyDtoOut(id);
        if (facultyDtoResponse == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(facultyDtoResponse);
    }

    @GetMapping
    public ResponseEntity<List<FacultyDtoResponse>> getAllFaculties() {
        List<FacultyDtoResponse> allFacultiesDto = facultyService.getAllFaculties();
        return ResponseEntity.ok(allFacultiesDto);
    }

    @GetMapping("/filter/{color}")
    public ResponseEntity<List<FacultyDtoResponse>> getFacultiesByColor(@PathVariable String color) {
        List<FacultyDtoResponse> allFacultiesDtoByColor = facultyService.filterByColor(color);
        return ResponseEntity.ok(allFacultiesDtoByColor);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FacultyDtoResponse>> getFacultiesByColorOrName
            (@RequestParam String request) {
        List<FacultyDtoResponse> allFacultiesDtoByColorOrName = facultyService.getFacultiesByColorOrName(request);
        return ResponseEntity.ok(allFacultiesDtoByColorOrName);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<List<StudentDtoResponse>> getAllStudents(@PathVariable Long id) {
        Faculty faculty = facultyService.getFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        List<StudentDtoResponse> studentDtoResponses = facultyService.getStudentDtos(faculty);
        return ResponseEntity.ok(studentDtoResponses);
    }

    @PostMapping
    public ResponseEntity<FacultyDtoResponse> addFaculty(@RequestBody FacultyDtoIn facultyDtoIn) {
        FacultyDtoResponse facultyDtoResponse = facultyService.addFaculty(facultyDtoIn);
        return ResponseEntity.ok(facultyDtoResponse);
    }

    @PutMapping
    public ResponseEntity<FacultyDtoResponse> editFaculty(@RequestBody FacultyDtoInWithId facultyDtoInWithId) {
        Faculty getFaculty = facultyService.getFaculty(facultyDtoInWithId.getId());
        if (getFaculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        FacultyDtoResponse foundFaculty = facultyService.editFaculty(facultyDtoInWithId);
        return ResponseEntity.ok(foundFaculty);
    }

    @PutMapping("add_student")
    public ResponseEntity<FacultyDtoResponse> addStudentToFaculty(@RequestParam long studentId, @RequestParam long facultyId) {
        Faculty faculty = facultyService.getFaculty(facultyId);
        Student student = studentService.getStudent(studentId);
        if (faculty == null || student == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        FacultyDtoResponse facultyDtoResponse = facultyService.addStudentToFaculty(student, faculty);
        return ResponseEntity.ok(facultyDtoResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<FacultyDtoResponse> deleteFaculty(@PathVariable long id) {
        Faculty getFaculty = facultyService.getFaculty(id);
        if (getFaculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        facultyService.removeFaculty(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<FacultyDtoResponse> deleteAllFaculties() {
        facultyService.clearAll();
        return ResponseEntity.ok().build();
    }
}
