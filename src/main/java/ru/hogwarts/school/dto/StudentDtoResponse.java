package ru.hogwarts.school.dto;

import java.util.Objects;

public class StudentDtoResponse {
    private long id;
    private String name;
    private int age;
    private long facultyId;

    public StudentDtoResponse() {
    }

    public StudentDtoResponse(long id, String name, int age, long facultyId) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.facultyId = facultyId;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(long facultyId) {
        this.facultyId = facultyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentDtoResponse that)) return false;
        return id == that.id && age == that.age && facultyId == that.facultyId && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, facultyId);
    }
}
