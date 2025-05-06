package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public FacultyService(FacultyRepository facultyRepository, FacultyDTOMapper facultyDTOMapper,
                          StudentService studentService, StudentDTOMapper studentDTOMapper) {
        this.facultyRepository = facultyRepository;
        this.facultyDTOMapper = facultyDTOMapper;
        this.studentService = studentService;
        this.studentDTOMapper = studentDTOMapper;
    }

    public FacultyDtoResponse addFaculty(FacultyDtoIn facultyDtoIn) {
        logger.info("Method invoked to add faculty.");
        return facultyDTOMapper.facultyToDtoOut(facultyRepository.save(facultyDTOMapper.dtoInToFaculty(facultyDtoIn)));
    }

    public void removeFaculty(long id) {
        logger.info("Method invoked to remove faculty.");
        facultyRepository.deleteById(id);
    }

    public void clearAll() {
        logger.info("Method invoked to remove all faculties.");
        facultyRepository.deleteAll();
    }

    public Faculty getFaculty(long id) {
        logger.info("Method invoked to get faculty.");
        return facultyRepository.findById(id).orElse(null);
    }

    public FacultyDtoResponse getFacultyDtoOut(long id) {
        logger.info("Method invoked to get faculty DTO.");
        return facultyDTOMapper.facultyToDtoOut(facultyRepository.findById(id).orElse(null));
    }

    public Faculty editFaculty(Faculty faculty) {
        logger.info("Method invoked to edit faculty.");
        return facultyRepository.save(faculty);
    }

    public FacultyDtoResponse editFaculty(FacultyDtoInWithId facultyDtoInWithId) {
        logger.info("Method invoked to edit faculty and return faculty DTO.");
        return facultyDTOMapper.facultyToDtoOut(facultyRepository.save(facultyDTOMapper.dtoInWithIdToFaculty(facultyDtoInWithId)));
    }

    public List<FacultyDtoResponse> getAllFaculties() {
        logger.info("Method invoked to get all faculties.");
        return facultyRepository.findAll().stream().map(facultyDTOMapper::facultyToDtoOut).toList();
    }

    public List<FacultyDtoResponse> filterByColor(String color) {
        logger.info("Method invoked to get faculties by color.");
        return facultyRepository.findByColor(color).stream().map(facultyDTOMapper::facultyToDtoOut).toList();
    }

    public List<FacultyDtoResponse> getFacultiesByColorOrName(String request) {
        logger.info("Method invoked to get faculties by color or name.");
        return facultyRepository.findByColorIgnoreCaseOrNameIgnoreCase(request, request).stream().map(facultyDTOMapper::facultyToDtoOut).toList();
    }

    public FacultyDtoResponse addStudentToFaculty(long studentId, long facultyId) {
        logger.info("Method invoked to add student to faculty.");
        Faculty faculty = getFaculty(facultyId);
        Student student = studentService.getStudent(studentId);
        if (faculty == null || student == null) {
            return null;
        }
        faculty.addStudent(student);
        student.setFaculty(faculty);
        editFaculty(faculty);
        studentService.editStudent(student);
        return facultyDTOMapper.facultyToDtoOut(faculty);
    }

    public List<StudentDtoResponse> getStudentDtos(Faculty faculty) {
        logger.info("Method invoked to get student DTOs from faculty.");
        return faculty.getStudents().stream().map(studentDTOMapper::studentToDtoOut).toList();
    }
}
