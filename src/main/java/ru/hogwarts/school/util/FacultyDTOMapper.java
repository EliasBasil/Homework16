package ru.hogwarts.school.util;

import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.FacultyDtoInWithId;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

@Component
public class FacultyDTOMapper {
    StudentService studentService;
    StudentDTOMapper studentDTOMapper;

    public FacultyDTOMapper(StudentService studentService, StudentDTOMapper studentDTOMapper) {
        this.studentService = studentService;
        this.studentDTOMapper = studentDTOMapper;
    }

    public FacultyDtoOut facultyToDtoOut(Faculty faculty) {
        FacultyDtoOut facultyDtoOut = new FacultyDtoOut(faculty.getId(), faculty.getName(), faculty.getColor());
        for (Student s : faculty.getStudents()) {
            facultyDtoOut.addStudentDTO(studentDTOMapper.studentToDtoOut(s));
        }
        return facultyDtoOut;
    }

    public Faculty dtoInToFaculty(FacultyDtoIn facultyDtoIn) {
        return new Faculty(facultyDtoIn.getName(), facultyDtoIn.getColor());
    }

    public Faculty dtoInWithIdToFaculty(FacultyDtoInWithId facultyDtoInWithId) {
        return new Faculty(facultyDtoInWithId.getId(), facultyDtoInWithId.getName(), facultyDtoInWithId.getColor());
    }
}
