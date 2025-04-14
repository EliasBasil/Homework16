package ru.hogwarts.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Collection<Faculty> findByColor(String color);

    Collection<Faculty> findByColorIgnoreCaseOrNameIgnoreCase(String requestOne, String requestTwo);

}
