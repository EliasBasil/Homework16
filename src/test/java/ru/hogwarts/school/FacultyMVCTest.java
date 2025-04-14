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
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.repositories.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.util.FacultyDTOMapper;
import ru.hogwarts.school.util.StudentDTOMapper;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan(basePackages = "ru.hogwarts.school")
@WebMvcTest(StudentController.class)
public class FacultyMVCTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private FacultyRepository facultyRepository;

    @MockitoSpyBean
    private FacultyService facultyService;

    @MockitoSpyBean
    private StudentDTOMapper studentDTOMapper;

    @MockitoSpyBean
    private FacultyDTOMapper facultyDTOMapper;

    private FacultyController facultyController;


    @Test
    public void testPostFaculty() throws Exception {
        String name = "Name";
        String color = "color";

        JSONObject facultyDtoInObject = new JSONObject();
        facultyDtoInObject.put("name", name);
        facultyDtoInObject.put("color", color);

        Faculty faculty = new Faculty(name, color);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyDtoInObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void testGetFacultyInfo_whenFacultyExists() throws Exception {
        long id = 12;
        String name = "Name";
        String color = "color";
        Faculty faculty = new Faculty(id, name, color);

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void testGetFacultyInfo_whenFacultyDoesNotExist() throws Exception {
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllFaculties() throws Exception {
        int numberOfFacultiesForTest = 4;
        Faculty faculty1 = new Faculty(1, "Name", "color");
        Faculty faculty2 = new Faculty(2, "Name", "color");
        Faculty faculty3 = new Faculty(3, "Name", "color");
        Faculty faculty4 = new Faculty(4, "Name", "color");
        ArrayList<Faculty> list = new ArrayList<>();
        list.add(faculty1);
        list.add(faculty2);
        list.add(faculty3);
        list.add(faculty4);

        when(facultyRepository.findAll()).thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfFacultiesForTest)));
    }

    @Test
    public void testGetFacultiesByColorOrName() throws Exception {
        String searchTerm = "lavender";
        int numberOfFacultiesForTest = 3;

        Faculty faculty1 = new Faculty(1, "Name", searchTerm);
        Faculty faculty2 = new Faculty(2, searchTerm, "color");
        Faculty faculty3 = new Faculty(3, "Name", searchTerm);
        ArrayList<Faculty> list = new ArrayList<>();
        list.add(faculty1);
        list.add(faculty2);
        list.add(faculty3);

        when(facultyRepository.findByColorIgnoreCaseOrNameIgnoreCase(searchTerm, searchTerm)).thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/search?request=" + searchTerm)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfFacultiesForTest)));
    }

    @Test
    public void testGetFacultyByColor() throws Exception {
        String searchTerm = "lavender";
        int numberOfFacultiesForTest = 2;

        Faculty faculty1 = new Faculty(1, "Name", searchTerm);
        Faculty faculty2 = new Faculty(2, "Name", searchTerm);
        ArrayList<Faculty> list = new ArrayList<>();
        list.add(faculty1);
        list.add(faculty2);

        when(facultyRepository.findByColor(searchTerm)).thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/filter/" + searchTerm)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfFacultiesForTest)));
    }

    @Test
    public void testEditFaculty_whenFacultyExists() throws Exception {
        long idBeforeEditing = 2;
        long idAfterEditing = 10;
        String name = "Name";
        String color = "color";
        Faculty facultyReturnedAfterEditing = new Faculty(idAfterEditing, name, color);
        Faculty facultyBeforeEditing = new Faculty(idBeforeEditing, name, color);

        JSONObject facultyDtoOutObject = new JSONObject();
        facultyDtoOutObject.put("id", idBeforeEditing);
        facultyDtoOutObject.put("name", name);
        facultyDtoOutObject.put("color", color);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(facultyReturnedAfterEditing);
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(facultyBeforeEditing));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyDtoOutObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idAfterEditing))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void testEditFaculty_whenFacultyDoesNotExist() throws Exception {
        long idBeforeEditing = 2;
        long idAfterEditing = 10;
        String name = "Name";
        String color = "color";
        Faculty facultyReturnedAfterEditing = new Faculty(idAfterEditing, name, color);

        JSONObject facultyDtoOutObject = new JSONObject();
        facultyDtoOutObject.put("id", idBeforeEditing);
        facultyDtoOutObject.put("name", name);
        facultyDtoOutObject.put("color", color);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(facultyReturnedAfterEditing);
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyDtoOutObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteFaculty_whenFacultyExists() throws Exception {
        long id = 12;
        String name = "Name";
        String color = "color";
        Faculty faculty = new Faculty(id, name, color);

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteFaculty_whenFacultyDoesNotExist() throws Exception {
        long id = 2;

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddStudentToFaculty() throws Exception {
        long facultyId = 1;
        String facultyName = "Name";
        String color = "color";
        Faculty faculty = new Faculty(facultyId, facultyName, color);

        long studentId = 1;
        String studentName = "Name";
        int age = 12;
        Student student = new Student(studentId, studentName, age, faculty);
        faculty.addStudent(student);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty/add_student?studentId=" + studentId + "&facultyId=" + facultyId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(facultyId))
                .andExpect(jsonPath("$.name").value(facultyName))
                .andExpect(jsonPath("$.color").value(color))
                .andExpect(jsonPath("$.students", hasSize(1)))
                .andExpect(jsonPath("$.students[0].id").value(studentId))
                .andExpect(jsonPath("$.students[0].name").value(studentName))
                .andExpect(jsonPath("$.students[0].age").value(age));
    }

    @Test
    public void testGetAllStudents() throws Exception {
        long facultyId = 1;
        String facultyName = "Name";
        String color = "color";
        Faculty faculty = new Faculty(facultyId, facultyName, color);

        long studentId1 = 1;
        String studentName1 = "Name";
        int age1 = 12;
        Student student1 = new Student(studentId1, studentName1, age1, faculty);
        faculty.addStudent(student1);

        long studentId2 = 2;
        String studentName2 = "Name";
        int age2 = 12;
        Student student2 = new Student(studentId2, studentName2, age2, faculty);
        faculty.addStudent(student2);

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/students/" + facultyId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id").value(studentId1))
                .andExpect(jsonPath("$.[0].name").value(studentName1))
                .andExpect(jsonPath("$.[0].age").value(age1))
                .andExpect(jsonPath("$.[1].id").value(studentId2))
                .andExpect(jsonPath("$.[1].name").value(studentName2))
                .andExpect(jsonPath("$.[1].age").value(age2));
    }
}
