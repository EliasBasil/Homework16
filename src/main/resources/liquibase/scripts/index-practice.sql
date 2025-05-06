-- liquibase formatted sql

-- changeset evasiliev:1
CREATE INDEX student_name ON student (name);
-- changeset evasiliev:2
CREATE INDEX faculty_name_color ON faculty (name, color);