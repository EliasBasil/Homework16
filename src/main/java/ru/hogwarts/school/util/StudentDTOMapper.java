package ru.hogwarts.school.util;

import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.StudentDtoResponse;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.repositories.FacultyRepository;

@Component
public class StudentDTOMapper {
    FacultyRepository facultyRepository;

    public StudentDTOMapper(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public StudentDtoResponse studentToDtoOut(Student student) {
        if (student == null) {
            return null;
        }
        long id = student.getFaculty() == null ? 0 : student.getFaculty().getId();
        return new StudentDtoResponse(student.getId(), student.getName(), student.getAge(), id);
    }

    public Student dtoInToStudent(StudentDtoIn studentDtoIn) {
        return new Student(studentDtoIn.getName(), studentDtoIn.getAge());
    }

    public Student dtoOutToStudent(StudentDtoResponse studentDtoResponse) {
        return new Student(studentDtoResponse.getId(), studentDtoResponse.getName(), studentDtoResponse.getAge());
    }
}
