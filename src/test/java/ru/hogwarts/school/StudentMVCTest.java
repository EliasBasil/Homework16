package ru.hogwarts.school;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.repositories.StudentRepository;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.util.FacultyDTOMapper;
import ru.hogwarts.school.util.StudentDTOMapper;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan(basePackages = "ru.hogwarts.school")
@WebMvcTest(StudentController.class)
public class StudentMVCTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private FacultyRepository facultyRepository;

    @MockitoSpyBean
    private StudentService studentService;

    @MockitoSpyBean
    private StudentDTOMapper studentDTOMapper;

    @MockitoSpyBean
    private FacultyDTOMapper facultyDTOMapper;

    private StudentController studentController;


    @Test
    public void testPostStudent() throws Exception {
        String name = "Name";
        int age = 12;

        JSONObject studentDtoInObject = new JSONObject();
        studentDtoInObject.put("name", name);
        studentDtoInObject.put("age", age);

        Student student = new Student(name, age);

        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentDtoInObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    @Test
    public void testGetStudentById_whenStudentExists() throws Exception {
        long id = 1;
        String name = "Name";
        int age = 12;
        Student student = new Student(id, name, age);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    @Test
    public void testGetStudentInfoById_whenStudentDoesNotExist() throws Exception {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllStudents() throws Exception {
        int numberOfStudentsForTest = 4;
        Student student1 = new Student(1, "Name", 10);
        Student student2 = new Student(2, "Name", 12);
        Student student3 = new Student(3, "Name", 13);
        Student student4 = new Student(4, "Name", 17);
        ArrayList<Student> list = new ArrayList<>();
        list.add(student1);
        list.add(student2);
        list.add(student3);
        list.add(student4);
        when(studentRepository.findAll()).thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfStudentsForTest)));
    }

    @Test
    public void testGetStudentsByAgeBetween() throws Exception {
        int minAge = 11;
        int maxAge = 14;
        int numberOfStudentsForTest = 2;

        Student student1 = new Student(2, "Name", 12);
        Student student2 = new Student(3, "Name", 13);
        ArrayList<Student> list = new ArrayList<>();
        list.add(student1);
        list.add(student2);
        when(studentRepository.findAllByAgeBetween(anyInt(), anyInt())).thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/filter?minAge=" + minAge + "&maxAge=" + maxAge)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfStudentsForTest)));
    }

    @Test
    public void testGetStudentsByAge() throws Exception {
        int testedAge = 10;
        int numberOfStudentsForTest = 2;

        Student student1 = new Student(2, "Name", 10);
        Student student2 = new Student(3, "Name", 10);
        ArrayList<Student> list = new ArrayList<>();
        list.add(student1);
        list.add(student2);
        when(studentRepository.findByAge(anyInt())).thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/filter/" + testedAge)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfStudentsForTest)));
    }

    @Test
    public void testEditStudent_whenStudentExists() throws Exception {
        long idBeforeEditing = 2;
        long idAfterEditing = 10;
        String name = "Name";
        int age = 12;
        Student studentReturnedAfterEditing = new Student(idAfterEditing, name, age);
        Student studentBeforeEditing = new Student(idBeforeEditing, name, age);

        JSONObject studentDtoOutObject = new JSONObject();
        studentDtoOutObject.put("id", idBeforeEditing);
        studentDtoOutObject.put("name", name);
        studentDtoOutObject.put("age", age);

        when(studentRepository.save(any(Student.class))).thenReturn(studentReturnedAfterEditing);
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(studentBeforeEditing));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(studentDtoOutObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idAfterEditing))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    @Test
    public void testEditStudent_whenStudentDoesNotExist() throws Exception {
        long idBeforeEditing = 2;
        long idAfterEditing = 10;
        String name = "Name";
        int age = 12;
        Student studentReturnedAfterEditing = new Student(idAfterEditing, name, age);

        JSONObject studentDtoOutObject = new JSONObject();
        studentDtoOutObject.put("id", idBeforeEditing);
        studentDtoOutObject.put("name", name);
        studentDtoOutObject.put("age", age);

        when(studentRepository.save(any(Student.class))).thenReturn(studentReturnedAfterEditing);
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(studentDtoOutObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteStudent_whenStudentExists() throws Exception {
        long id = 2;
        String name = "Name";
        int age = 12;
        Student student = new Student(id, name, age);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteStudent_whenStudentDoesNotExist() throws Exception {
        long id = 2;

        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetFacultyFromStudent() throws Exception {
        long facultyId = 1;
        String facultyName = "Name";
        String color = "color";
        Faculty faculty = new Faculty(facultyId, facultyName, color);

        long studentId = 1;
        String studentName = "Name";
        int age = 12;
        Student student = new Student(studentId, studentName, age, faculty);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/faculty/" + studentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(facultyId))
                .andExpect(jsonPath("$.name").value(facultyName))
                .andExpect(jsonPath("$.color").value(color));
    }
}
