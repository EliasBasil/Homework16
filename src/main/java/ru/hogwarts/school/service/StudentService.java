package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.Collection;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public void removeStudent(long id) {
        studentRepository.deleteById(id);
    }

    public Student getStudent(Long id) {
        return studentRepository.findById(id).get();
    }

    public Student editStudent(Student student) {
        return studentRepository.save(student);
    }

    public Collection<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Collection<Student> filterStudentsByAge(int age) {
        return studentRepository.findByAge(age);
    }
}
