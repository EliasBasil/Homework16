package ru.hogwarts.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAge(int age);

    Collection<Student> findAllByAgeBetween(int minAge, int maxAge);

    @Query(value = "SELECT COUNT(*) AS total FROM student", nativeQuery = true)
    Integer getTotalNumberOfStudents();

    @Query(value = "SELECT AVG(age) AS average_age FROM student", nativeQuery = true)
    Double getAverageAgeOfStudents();

    @Query(value = "SELECT * FROM student ORDER BY id DESC LIMIT 5", nativeQuery = true)
    List<Student> getLatestFiveOfStudents();
}
