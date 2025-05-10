package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoResponse;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;
import ru.hogwarts.school.util.StudentDTOMapper;

import java.util.List;
import java.util.OptionalDouble;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentDTOMapper studentDTOMapper;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository studentRepository, StudentDTOMapper studentDTOMapper) {
        this.studentRepository = studentRepository;
        this.studentDTOMapper = studentDTOMapper;
    }

    public StudentDtoResponse addStudent(StudentDtoIn studentDtoIn) {
        logger.info("Method invoked to create student.");
        return studentDTOMapper.studentToDtoOut(studentRepository.save(studentDTOMapper.dtoInToStudent(studentDtoIn)));
    }

    public void removeStudent(long id) {
        logger.info("Method invoked to remove student.");
        studentRepository.deleteById(id);
    }

    public void clearAll() {
        logger.info("Method invoked to delete all students.");
        studentRepository.deleteAll();
    }

    public Student getStudent(Long id) {
        logger.info("Method invoked to get student.");
        return studentRepository.findById(id).orElse(null);
    }

    public StudentDtoResponse getStudentDtoOut(Long id) {
        logger.info("Method invoked to get student DTO.");
        Student student = studentRepository.findById(id).orElse(null);
        return studentDTOMapper.studentToDtoOut(student);
    }

    public Student editStudent(Student student) {
        logger.info("Method invoked to edit student.");
        return studentRepository.save(student);
    }

    public StudentDtoResponse editStudent(StudentDtoResponse studentDtoResponse) {
        logger.info("Method invoked to edit student and return DTO.");
        return studentDTOMapper.studentToDtoOut(studentRepository.save(studentDTOMapper.dtoOutToStudent(studentDtoResponse)));
    }

    public List<StudentDtoResponse> getAllStudents() {
        logger.info("Method invoked to get all students.");
        return studentRepository.findAll().stream().map(studentDTOMapper::studentToDtoOut).toList();
    }

    public List<StudentDtoResponse> filterStudentsByAge(int age) {
        logger.info("Method invoked to get students by age.");
        return studentRepository.findByAge(age).stream().map(studentDTOMapper::studentToDtoOut).toList();
    }

    public List<StudentDtoResponse> getStudentsByAgeBetween(int minAge, int maxAge) {
        logger.info("Method invoked to get students by age between two values.");
        return studentRepository.findAllByAgeBetween(minAge, maxAge).stream().map(studentDTOMapper::studentToDtoOut).toList();
    }

    public Integer getTotalNumberOfStudents() {
        logger.info("Method invoked to get total number of students.");
        return studentRepository.getTotalNumberOfStudents();
    }

    public Double getAverageAgeOfStudents() {
        logger.info("Method invoked to get average age  of students.");
        return studentRepository.getAverageAgeOfStudents();
    }

    public List<StudentDtoResponse> getLatestFiveOfStudents() {
        logger.info("Method invoked to get latest five students.");
        return studentRepository.getLatestFiveOfStudents().stream().map(studentDTOMapper::studentToDtoOut).toList();
    }

    public List<String> getStudentsStartingWithA() {
        logger.info("Method invoked to get all students' names that start with an \"A\".");
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name.startsWith("A"))
                .sorted()
                .map(String::toUpperCase)
                .toList();
    }

    public Double getAverageAge() {
        logger.info("Method invoked to get average age of students");
        OptionalDouble average = studentRepository.findAll().stream()
                .mapToDouble(Student::getAge)
                .average();
        return average.isPresent() ? average.getAsDouble() : 0;
    }

    public void printParallel() {
        List<StudentDtoResponse> students = getAllStudents();
        if (students.size() < 6) {
            throw new RuntimeException("Not enough students in the database");
        }
        Thread thread1 = new Thread() {
            @Override
            public void start() {
                System.out.println(students.get(2));
                System.out.println(students.get(3));
            }
        };
        Thread thread2 = new Thread() {
            @Override
            public void start() {
                System.out.println(students.get(4));
                System.out.println(students.get(5));
            }
        };

        System.out.println(students.get(0));
        thread1.start();
        System.out.println(students.get(1));
        thread2.start();
    }

    public void printSynchronized() {
        List<StudentDtoResponse> students = getAllStudents();
        if (students.size() < 6) {
            throw new RuntimeException("Not enough students in the database");
        }
        Thread thread1 = new Thread() {
            @Override
            public void start() {
                consoleOutput(students.get(2));
                consoleOutput(students.get(3));
            }
        };
        Thread thread2 = new Thread() {
            @Override
            public void start() {
                consoleOutput(students.get(4));
                consoleOutput(students.get(5));
            }
        };

        consoleOutput(students.get(0));
        thread1.start();
        consoleOutput(students.get(1));
        thread2.start();
    }

    private synchronized void consoleOutput(StudentDtoResponse output) {
        System.out.println(output);
    }
}
