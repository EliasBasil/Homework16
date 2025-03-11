package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final Map<Long, Student> students = new HashMap<>();
    private static Long id = 0L;

    public StudentService() {

    }

    public Student addStudent(Student student) {
        student.setId(id++);
        students.put(student.getId(), student);
        return student;
    }

    public Student removeStudent(long id) {
        return students.remove(id);
    }

    public Student getStudent(long id) {
        return students.get(id);
    }

    public Student editStudent(Student student) {
        if (students.containsKey(student.getId())) {
            students.put(student.getId(), student);
            return student;
        }
        return null;
    }

    public Collection<Student> getAllStudents() {
        return students.values();
    }

    public Collection<Student> filterStudentsByAge(int age) {
        return getAllStudents().stream().filter(p -> p.getAge() == age).collect(Collectors.toList());
    }
}
