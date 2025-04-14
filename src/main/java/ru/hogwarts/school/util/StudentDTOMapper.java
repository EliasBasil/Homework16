package ru.hogwarts.school.util;

import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.repositories.FacultyRepository;

@Component
public class StudentDTOMapper {
    FacultyRepository facultyRepository;

    public StudentDTOMapper(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public StudentDtoOut studentToDtoOut(Student student) {
        long id = student.getFaculty() == null ? 0 : student.getFaculty().getId();
        return new StudentDtoOut(student.getId(), student.getName(), student.getAge(), id);
    }

    public Student dtoInToStudent(StudentDtoIn studentDtoIn) {
        return new Student(studentDtoIn.getName(), studentDtoIn.getAge());
    }

    public Student dtoOutToStudent(StudentDtoOut studentDtoOut) {
        return new Student(studentDtoOut.getId(), studentDtoOut.getName(), studentDtoOut.getAge());
    }
}
