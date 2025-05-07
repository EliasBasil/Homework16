package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.dto.FacultyDtoInWithId;
import ru.hogwarts.school.dto.FacultyDtoResponse;
import ru.hogwarts.school.dto.StudentDtoResponse;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;
    private static final Logger logger = LoggerFactory.getLogger(FacultyController.class);


    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("{id}")
    public ResponseEntity<FacultyDtoResponse> getFacultyInfo(@PathVariable Long id) {
        FacultyDtoResponse facultyDtoResponse = facultyService.getFacultyDtoOut(id);
        if (facultyDtoResponse == null) {
            logger.warn("Trying to get faculty with nonexistent id = " + id);
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
            logger.warn("Trying to get all students of a faculty with nonexistent id = " + id);
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
            logger.warn("Trying to edit faculty with nonexistent id = " + facultyDtoInWithId.getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        FacultyDtoResponse foundFaculty = facultyService.editFaculty(facultyDtoInWithId);
        return ResponseEntity.ok(foundFaculty);
    }

    @PutMapping("add_student")
    public ResponseEntity<FacultyDtoResponse> addStudentToFaculty(@RequestParam long studentId, @RequestParam long facultyId) {
        FacultyDtoResponse facultyDtoResponse = facultyService.addStudentToFaculty(studentId, facultyId);
        if (facultyDtoResponse == null) {
            logger.warn("Trying to add student to a faculty with either nonexistent student id = " + studentId +
                    " or with nonexistent faculty id = " + facultyId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(facultyDtoResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<FacultyDtoResponse> deleteFaculty(@PathVariable long id) {
        Faculty getFaculty = facultyService.getFaculty(id);
        if (getFaculty == null) {
            logger.warn("Trying to delete faculty with nonexistent id = " + id);
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
