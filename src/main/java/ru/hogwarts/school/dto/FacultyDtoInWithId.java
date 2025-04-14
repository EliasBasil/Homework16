package ru.hogwarts.school.dto;

import java.util.Objects;

public class FacultyDtoInWithId extends FacultyDtoIn {
    private long id;

    public FacultyDtoInWithId(String name, String color, long id) {
        super(name, color);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FacultyDtoInWithId that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
