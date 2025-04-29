package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoResponse;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;
import ru.hogwarts.school.util.StudentDTOMapper;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentDTOMapper studentDTOMapper;

    public StudentService(StudentRepository studentRepository, StudentDTOMapper studentDTOMapper) {
        this.studentRepository = studentRepository;
        this.studentDTOMapper = studentDTOMapper;
    }

    public StudentDtoResponse addStudent(StudentDtoIn studentDtoIn) {
        return studentDTOMapper.studentToDtoOut(studentRepository.save(studentDTOMapper.dtoInToStudent(studentDtoIn)));
    }

    public void removeStudent(long id) {
        studentRepository.deleteById(id);
    }

    public void clearAll() {
        studentRepository.deleteAll();
    }

    public Student getStudent(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public StudentDtoResponse getStudentDtoOut(Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        return studentDTOMapper.studentToDtoOut(student);
    }

    public Student editStudent(Student student) {
        return studentRepository.save(student);
    }

    public StudentDtoResponse editStudent(StudentDtoResponse studentDtoResponse) {
        return studentDTOMapper.studentToDtoOut(studentRepository.save(studentDTOMapper.dtoOutToStudent(studentDtoResponse)));
    }

    public List<StudentDtoResponse> getAllStudents() {
        return studentRepository.findAll().stream().map(studentDTOMapper::studentToDtoOut).toList();
    }

    public List<StudentDtoResponse> filterStudentsByAge(int age) {
        return studentRepository.findByAge(age).stream().map(studentDTOMapper::studentToDtoOut).toList();
    }

    public List<StudentDtoResponse> getStudentsByAgeBetween(int minAge, int maxAge) {
        return studentRepository.findAllByAgeBetween(minAge, maxAge).stream().map(studentDTOMapper::studentToDtoOut).toList();
    }

    public Integer getTotalNumberOfStudents() {
        return studentRepository.getTotalNumberOfStudents();
    }

    public Double getAverageAgeOfStudents() {
        return studentRepository.getAverageAgeOfStudents();
    }

    public List<StudentDtoResponse> getLatestFiveOfStudents() {
        return studentRepository.getLatestFiveOfStudents().stream().map(studentDTOMapper::studentToDtoOut).toList();
    }
}
