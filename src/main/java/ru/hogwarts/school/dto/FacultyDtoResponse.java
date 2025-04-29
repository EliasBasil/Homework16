package ru.hogwarts.school.dto;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FacultyDtoResponse {
    private long id;
    private String name;
    private String color;
    private final Set<StudentDtoResponse> students = new HashSet<>();

    public FacultyDtoResponse() {
    }

    public FacultyDtoResponse(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Set<StudentDtoResponse> getStudents() {
        return students;
    }

    public void addStudentDTO(StudentDtoResponse studentDtoResponse) {
        students.add(studentDtoResponse);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FacultyDtoResponse that)) return false;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(color, that.color) && Objects.equals(students, that.students);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, students);
    }
}
