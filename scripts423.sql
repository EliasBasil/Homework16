SELECT student.name, student.age, faculty.name FROM student JOIN faculty ON student.faculty_id = faculty.id;
SELECT student.name, student.age FROM student INNER JOIN avatar ON student.avatar_id = id;