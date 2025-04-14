package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repositories.FacultyRepository;

import java.util.Collection;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public void removeFaculty(long id) {
        facultyRepository.deleteById(id);
    }

    public void clearAll() {
        facultyRepository.deleteAll();
    }

    public Faculty getFaculty(long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty editFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Collection<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Collection<Faculty> filterByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    public Collection<Faculty> getFacultiesByColorOrName(String request) {
        return facultyRepository.findByColorIgnoreCaseOrNameIgnoreCase(request, request);
    }
}
