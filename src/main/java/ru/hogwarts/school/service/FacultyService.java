package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    private final Map<Long, Faculty> faculties = new HashMap<>();
    private static Long id = 0L;

    public FacultyService() {

    }

    public Faculty addFaculty(Faculty faculty) {
        faculty.setId(id++);
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty removeFaculty(long id) {
        return faculties.remove(id);
    }

    public Faculty getFaculty(long id) {
        return faculties.get(id);
    }

    public Faculty editFaculty(Faculty faculty) {
        if (faculties.containsKey(faculty.getId())) {
            faculties.put(faculty.getId(), faculty);
            return faculty;
        }
        return null;
    }

    public Collection<Faculty> getAllFaculties() {
        return faculties.values();
    }

    public Collection<Faculty> filterByColor(String color) {
        return getAllFaculties().stream().filter(p -> p.getColor().equals(color)).collect(Collectors.toList());
    }
}
