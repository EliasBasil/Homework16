package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.dto.FacultyDtoInWithId;
import ru.hogwarts.school.dto.FacultyDtoResponse;
import ru.hogwarts.school.dto.StudentDtoResponse;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.util.FacultyDTOMapper;
import ru.hogwarts.school.util.StudentDTOMapper;

import java.util.List;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final FacultyDTOMapper facultyDTOMapper;
    private final StudentService studentService;
    private final StudentDTOMapper studentDTOMapper;

    public FacultyService(FacultyRepository facultyRepository, FacultyDTOMapper facultyDTOMapper,
                          StudentService studentService, StudentDTOMapper studentDTOMapper) {
        this.facultyRepository = facultyRepository;
        this.facultyDTOMapper = facultyDTOMapper;
        this.studentService = studentService;
        this.studentDTOMapper = studentDTOMapper;
    }

    public FacultyDtoResponse addFaculty(FacultyDtoIn facultyDtoIn) {
        return facultyDTOMapper.facultyToDtoOut(facultyRepository.save(facultyDTOMapper.dtoInToFaculty(facultyDtoIn)));
    }

    public void removeFaculty(long id) {
        facultyRepository.deleteById(id);
    }

    public void clearAll() {
        facultyRepository.deleteAll();
    }

    public Faculty getFaculty(long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public FacultyDtoResponse getFacultyDtoOut(long id) {
        return facultyDTOMapper.facultyToDtoOut(facultyRepository.findById(id).orElse(null));
    }

    public Faculty editFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public FacultyDtoResponse editFaculty(FacultyDtoInWithId facultyDtoInWithId) {
        return facultyDTOMapper.facultyToDtoOut(facultyRepository.save(facultyDTOMapper.dtoInWithIdToFaculty(facultyDtoInWithId)));
    }

    public List<FacultyDtoResponse> getAllFaculties() {
        return facultyRepository.findAll().stream().map(facultyDTOMapper::facultyToDtoOut).toList();
    }

    public List<FacultyDtoResponse> filterByColor(String color) {
        return facultyRepository.findByColor(color).stream().map(facultyDTOMapper::facultyToDtoOut).toList();
    }

    public List<FacultyDtoResponse> getFacultiesByColorOrName(String request) {
        return facultyRepository.findByColorIgnoreCaseOrNameIgnoreCase(request, request).stream().map(facultyDTOMapper::facultyToDtoOut).toList();
    }

    public FacultyDtoResponse addStudentToFaculty(Student student, Faculty faculty) {
        faculty.addStudent(student);
        student.setFaculty(faculty);
        editFaculty(faculty);
        studentService.editStudent(student);
        return facultyDTOMapper.facultyToDtoOut(faculty);
    }

    public List<StudentDtoResponse> getStudentDtos(Faculty faculty) {
        return faculty.getStudents().stream().map(studentDTOMapper::studentToDtoOut).toList();
    }
}
