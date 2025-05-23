package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.dto.FacultyDtoResponse;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoResponse;
import ru.hogwarts.school.model.Faculty;

import java.util.ArrayList;

import static java.util.Objects.requireNonNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FacultyRestTemplateTest {
    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() throws Exception {
        Assertions.assertThat(facultyController).isNotNull();
    }

    @Test
    public void testPostFaculty() throws Exception {
        FacultyDtoIn facultyDtoIn = new FacultyDtoIn();
        facultyDtoIn.setColor("color");
        facultyDtoIn.setName("Name");

        ResponseEntity<FacultyDtoResponse> facultyResponseEntity = restTemplate
                .postForEntity("http://localhost:" + port + "/faculty", facultyDtoIn, FacultyDtoResponse.class);
        long savedFacultyId = requireNonNull(facultyResponseEntity.getBody()).getId();

        ResponseEntity<FacultyDtoResponse> findResponse = restTemplate
                .getForEntity("http://localhost:" + port + "/faculty/" + savedFacultyId, FacultyDtoResponse.class);
        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(findResponse.getBody().getId()).isEqualTo(savedFacultyId);
        Assertions.assertThat(findResponse.getBody().getColor()).isEqualTo("color");
        Assertions.assertThat(findResponse.getBody().getName()).isEqualTo("Name");

        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFacultyId);
    }

    @Test
    public void testGetFacultyInfo_whenFacultyExists() throws Exception {
        long savedFacultyId = postFacultyAndGetFacultysId("Name", "color");

        ResponseEntity<FacultyDtoResponse> findResponse = restTemplate
                .getForEntity("http://localhost:" + port + "/faculty/" + savedFacultyId, FacultyDtoResponse.class);
        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(findResponse.getBody().getId()).isEqualTo(savedFacultyId);
        Assertions.assertThat(findResponse.getBody().getColor()).isEqualTo("color");
        Assertions.assertThat(findResponse.getBody().getName()).isEqualTo("Name");

        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFacultyId);
    }

    @Test
    public void testGetFacultyInfo_whenFacultyDoesNotExist() throws Exception {
        ResponseEntity<FacultyDtoResponse> findResponse = restTemplate
                .getForEntity("http://localhost:" + port + "/faculty/" + 16, FacultyDtoResponse.class);
        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetAllFaculties() throws Exception {
        long savedFaculty1Id = postFacultyAndGetFacultysId("Name", "color");
        long savedFaculty2Id = postFacultyAndGetFacultysId("Name", "color");
        long savedFaculty3Id = postFacultyAndGetFacultysId("Name", "color");
        long savedFaculty4Id = postFacultyAndGetFacultysId("Name", "color");

        int totalFaculties = 4;

        ResponseEntity<ArrayList<FacultyDtoResponse>> findResponse = restTemplate
                .exchange(
                        "http://localhost:" + port + "/faculty",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );

        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(findResponse.getBody().size()).isEqualTo(totalFaculties);

        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFaculty1Id);
        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFaculty2Id);
        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFaculty3Id);
        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFaculty4Id);
    }

    @Test
    public void testGetFacultiesByColorOrName() throws Exception {
        String searchTerm = "lavender";
        int totalFaculties = 3;

        long savedFaculty1Id = postFacultyAndGetFacultysId("Name", searchTerm);
        long savedFaculty2Id = postFacultyAndGetFacultysId(searchTerm, "color");
        long savedFaculty3Id = postFacultyAndGetFacultysId("Name", searchTerm);
        long savedFaculty4Id = postFacultyAndGetFacultysId("Name", "color");

        ResponseEntity<ArrayList<Faculty>> findResponse = restTemplate
                .exchange(
                        "http://localhost:" + port + "/faculty/search?request=" + searchTerm,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(findResponse.getBody().size()).isEqualTo(totalFaculties);

        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFaculty1Id);
        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFaculty2Id);
        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFaculty3Id);
        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFaculty4Id);
    }

    @Test
    public void testGetFacultyByColor() throws Exception {
        String color = "lavender";
        int totalFaculties = 2;

        long savedFaculty1Id = postFacultyAndGetFacultysId("Name", color);
        long savedFaculty2Id = postFacultyAndGetFacultysId("Name", "color");
        long savedFaculty3Id = postFacultyAndGetFacultysId("Name", color);
        long savedFaculty4Id = postFacultyAndGetFacultysId("Name", "color");

        ResponseEntity<ArrayList<Faculty>> findResponse = restTemplate
                .exchange(
                        "http://localhost:" + port + "/faculty/filter/" + color,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(findResponse.getBody().size()).isEqualTo(totalFaculties);

        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFaculty1Id);
        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFaculty2Id);
        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFaculty3Id);
        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFaculty4Id);
    }

    @Test
    public void testEditFaculty_whenFacultyExists() throws Exception {
        long savedFacultyId = postFacultyAndGetFacultysId("Name", "color");

        ResponseEntity<FacultyDtoResponse> findResponse = restTemplate
                .getForEntity("http://localhost:" + port + "/faculty/" + savedFacultyId, FacultyDtoResponse.class);
        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        FacultyDtoResponse facultyAfterEditing = new FacultyDtoResponse();
        facultyAfterEditing.setColor("new color");
        facultyAfterEditing.setName("New Name");
        facultyAfterEditing.setId(savedFacultyId);

        restTemplate.put("http://localhost:" + port + "/faculty", facultyAfterEditing);

        ResponseEntity<FacultyDtoResponse> response = restTemplate
                .getForEntity("http://localhost:" + port + "/faculty/" + savedFacultyId, FacultyDtoResponse.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(response.getBody().getColor()).isEqualTo("new color");
        Assertions.assertThat(response.getBody().getName()).isEqualTo("New Name");

        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFacultyId);
    }

    @Test
    public void testEditFaculty_whenFacultyDoesNotExist() throws Exception {
        FacultyDtoResponse facultyAfterEditing = new FacultyDtoResponse();
        facultyAfterEditing.setColor("new color");
        facultyAfterEditing.setName("New Name");
        facultyAfterEditing.setId(256);

        RequestEntity<FacultyDtoResponse> requestEntity = RequestEntity
                .put("http://localhost:" + port + "/faculty")
                .body(facultyAfterEditing);
        ResponseEntity<FacultyDtoResponse> response = restTemplate.exchange(
                requestEntity,
                FacultyDtoResponse.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testDeleteFaculty_whenFacultyExists() throws Exception {
        long savedFacultyId = postFacultyAndGetFacultysId("Name", "color");

        ResponseEntity<FacultyDtoResponse> findResponse = restTemplate
                .getForEntity("http://localhost:" + port + "/faculty/" + savedFacultyId, FacultyDtoResponse.class);
        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFacultyId);

        ResponseEntity<FacultyDtoResponse> response = restTemplate
                .getForEntity("http://localhost:" + port + "/faculty/" + savedFacultyId, FacultyDtoResponse.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteFaculty_whenFacultyDoesNotExist() throws Exception {
        RequestEntity<Void> requestEntity = RequestEntity
                .delete("http://localhost:" + port + "/faculty/16").build();
        ResponseEntity<FacultyDtoResponse> response = restTemplate.exchange(
                requestEntity,
                FacultyDtoResponse.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testAddStudentToFaculty() throws Exception {
        long savedStudentId = postStudentAndGetStudentsId("Name", 12);

        ResponseEntity<StudentDtoResponse> findResponseStudent = restTemplate
                .getForEntity("http://localhost:" + port + "/student/" + savedStudentId, StudentDtoResponse.class);
        Assertions.assertThat(findResponseStudent.getStatusCode()).isEqualTo(HttpStatus.OK);

        long savedFacultyId = postFacultyAndGetFacultysId("Name", "color");

        ResponseEntity<FacultyDtoResponse> findResponseFaculty = restTemplate
                .getForEntity("http://localhost:" + port + "/faculty/" + savedFacultyId, FacultyDtoResponse.class);
        Assertions.assertThat(findResponseFaculty.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<FacultyDtoResponse> addStudentResponse = restTemplate
                .exchange(
                        "http://localhost:" + port + "/faculty/add_student?studentId=" + savedStudentId + "&facultyId=" + savedFacultyId,
                        HttpMethod.PUT,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
        Assertions.assertThat(addStudentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<StudentDtoResponse> responseStudentAfterAddingFaculty = restTemplate
                .getForEntity("http://localhost:" + port + "/student/" + savedStudentId, StudentDtoResponse.class);
        Assertions.assertThat(responseStudentAfterAddingFaculty.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<FacultyDtoResponse> responseFacultyAfterAddingStudent = restTemplate
                .getForEntity("http://localhost:" + port + "/faculty/" + savedFacultyId, FacultyDtoResponse.class);
        Assertions.assertThat(responseFacultyAfterAddingStudent.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(responseStudentAfterAddingFaculty.getBody().getFacultyId()).isEqualTo(savedFacultyId);
        Assertions.assertThat(responseFacultyAfterAddingStudent.getBody().getStudents()).contains(responseStudentAfterAddingFaculty.getBody());

        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudentId);
        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFacultyId);
    }

    @Test
    public void testGetAllStudents() throws Exception {
        long savedStudentId1 = postStudentAndGetStudentsId("Name", 12);
        long savedStudentId2 = postStudentAndGetStudentsId("Another Name", 15);

        long savedFacultyId = postFacultyAndGetFacultysId("Name", "color");

        ResponseEntity<FacultyDtoResponse> addStudentResponse1 = restTemplate
                .exchange(
                        "http://localhost:" + port + "/faculty/add_student?studentId=" + savedStudentId1 + "&facultyId=" + savedFacultyId,
                        HttpMethod.PUT,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
        Assertions.assertThat(addStudentResponse1.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<FacultyDtoResponse> addStudentResponse2 = restTemplate
                .exchange(
                        "http://localhost:" + port + "/faculty/add_student?studentId=" + savedStudentId2 + "&facultyId=" + savedFacultyId,
                        HttpMethod.PUT,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
        Assertions.assertThat(addStudentResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ArrayList<StudentDtoResponse>> responseFacultyAfterAddingStudents = restTemplate
                .exchange(
                        "http://localhost:" + port + "/faculty/students/" + savedFacultyId,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
        Assertions.assertThat(responseFacultyAfterAddingStudents.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<StudentDtoResponse> responseStudentAfterAddingFaculty1 = restTemplate
                .getForEntity("http://localhost:" + port + "/student/" + savedStudentId1, StudentDtoResponse.class);
        Assertions.assertThat(responseStudentAfterAddingFaculty1.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<StudentDtoResponse> responseStudentAfterAddingFaculty2 = restTemplate
                .getForEntity("http://localhost:" + port + "/student/" + savedStudentId2, StudentDtoResponse.class);
        Assertions.assertThat(responseStudentAfterAddingFaculty2.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(responseFacultyAfterAddingStudents.getBody()).contains(responseStudentAfterAddingFaculty1.getBody());
        Assertions.assertThat(responseFacultyAfterAddingStudents.getBody()).contains(responseStudentAfterAddingFaculty2.getBody());

        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudentId1);
        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudentId2);
        restTemplate.delete("http://localhost:" + port + "/faculty/" + savedFacultyId);
    }

    public long postStudentAndGetStudentsId(String name, int age) {
        StudentDtoIn studentDtoIn = new StudentDtoIn();
        studentDtoIn.setName(name);
        studentDtoIn.setAge(age);

        ResponseEntity<StudentDtoResponse> studentResponseEntity = restTemplate
                .postForEntity("http://localhost:" + port + "/student", studentDtoIn, StudentDtoResponse.class);
        return requireNonNull(studentResponseEntity.getBody()).getId();
    }

    public long postFacultyAndGetFacultysId(String name, String color) {
        FacultyDtoIn facultyDtoIn = new FacultyDtoIn();
        facultyDtoIn.setColor(color);
        facultyDtoIn.setName(name);

        ResponseEntity<FacultyDtoResponse> facultyResponseEntity = restTemplate
                .postForEntity("http://localhost:" + port + "/faculty", facultyDtoIn, FacultyDtoResponse.class);
        return requireNonNull(facultyResponseEntity.getBody()).getId();
    }
}
