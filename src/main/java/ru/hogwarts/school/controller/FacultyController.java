package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.dto.FacultyDtoInWithId;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.util.FacultyDTOMapper;
import ru.hogwarts.school.util.StudentDTOMapper;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;
    private final FacultyDTOMapper facultyDTOMapper;
    private final StudentDTOMapper studentDTOMapper;
    private final StudentService studentService;

    public FacultyController(FacultyService facultyService, FacultyDTOMapper facultyDTOMapper,
                             StudentDTOMapper studentDTOMapper, StudentService studentService) {
        this.facultyService = facultyService;
        this.facultyDTOMapper = facultyDTOMapper;
        this.studentDTOMapper = studentDTOMapper;
        this.studentService = studentService;
    }

    @GetMapping("{id}")
    public ResponseEntity<FacultyDtoOut> getFacultyInfo(@PathVariable Long id) {
        Faculty faculty = facultyService.getFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(facultyDTOMapper.facultyToDtoOut(faculty));
    }

    @GetMapping
    public ResponseEntity<List<FacultyDtoOut>> getAllFaculties() {
        List<FacultyDtoOut> allFacultiesDto = facultyService.getAllFaculties()
                .stream().map(facultyDTOMapper::facultyToDtoOut).toList();
        return ResponseEntity.ok(allFacultiesDto);
    }

    @GetMapping("/filter/{color}")
    public ResponseEntity<List<FacultyDtoOut>> getFacultiesByColor(@PathVariable String color) {
        List<FacultyDtoOut> allFacultiesDtoByColor = facultyService.filterByColor(color)
                .stream().map(facultyDTOMapper::facultyToDtoOut).toList();
        return ResponseEntity.ok(allFacultiesDtoByColor);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FacultyDtoOut>> getFacultiesByColorOrName
            (@RequestParam String request) {
        List<FacultyDtoOut> allFacultiesDtoByColorOrName = facultyService.getFacultiesByColorOrName(request)
                .stream().map(facultyDTOMapper::facultyToDtoOut).toList();
        return ResponseEntity.ok(allFacultiesDtoByColorOrName);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<List<StudentDtoOut>> getAllStudents(@PathVariable Long id) {
        Faculty faculty = facultyService.getFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        List<StudentDtoOut> studentDtoOuts = faculty.getStudents()
                .stream().map(studentDTOMapper::studentToDtoOut).toList();
        return ResponseEntity.ok(studentDtoOuts);
    }

    @PostMapping
    public ResponseEntity<FacultyDtoOut> addFaculty(@RequestBody FacultyDtoIn facultyDtoIn) {
        Faculty faculty = facultyService.addFaculty(facultyDTOMapper.dtoInToFaculty(facultyDtoIn));
        return ResponseEntity.ok(facultyDTOMapper.facultyToDtoOut(faculty));
    }

    @PutMapping
    public ResponseEntity<FacultyDtoOut> editFaculty(@RequestBody FacultyDtoInWithId facultyDtoInWithId) {
        Faculty faculty = facultyDTOMapper.dtoInWithIdToFaculty(facultyDtoInWithId);
        Faculty getFaculty = facultyService.getFaculty(faculty.getId());
        if (getFaculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Faculty foundFaculty = facultyService.editFaculty(faculty);
        return ResponseEntity.ok(facultyDTOMapper.facultyToDtoOut(foundFaculty));
    }

    @PutMapping("add_student")
    public ResponseEntity<FacultyDtoOut> addStudentToFaculty(@RequestParam long studentId, @RequestParam long facultyId) {
        Faculty faculty = facultyService.getFaculty(facultyId);
        Student student = studentService.getStudent(studentId);
        if (faculty == null || student == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        faculty.addStudent(student);
        student.setFaculty(faculty);
        facultyService.editFaculty(faculty);
        studentService.editStudent(student);
        return ResponseEntity.ok(facultyDTOMapper.facultyToDtoOut(faculty));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<FacultyDtoOut> deleteFaculty(@PathVariable long id) {
        Faculty getFaculty = facultyService.getFaculty(id);
        if (getFaculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        facultyService.removeFaculty(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<FacultyDtoOut> deleteAllFaculties() {
        facultyService.clearAll();
        return ResponseEntity.ok().build();
    }
}
