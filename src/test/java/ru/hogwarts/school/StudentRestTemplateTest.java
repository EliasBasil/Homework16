package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.dto.FacultyDtoResponse;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoResponse;

import java.util.ArrayList;

import static java.util.Objects.requireNonNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StudentRestTemplateTest {
    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() throws Exception {
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    public void testPostStudent() throws Exception {
        StudentDtoIn studentDtoIn = new StudentDtoIn();
        studentDtoIn.setName("Name");
        studentDtoIn.setAge(12);

        ResponseEntity<StudentDtoResponse> studentResponseEntity = restTemplate
                .postForEntity("http://localhost:" + port + "/student", studentDtoIn, StudentDtoResponse.class);
        long savedStudentId = requireNonNull(studentResponseEntity.getBody()).getId();

        ResponseEntity<StudentDtoResponse> findResponse = restTemplate
                .getForEntity("http://localhost:" + port + "/student/" + savedStudentId, StudentDtoResponse.class);
        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(findResponse.getBody().getId()).isEqualTo(savedStudentId);
        Assertions.assertThat(findResponse.getBody().getAge()).isEqualTo(12);
        Assertions.assertThat(findResponse.getBody().getName()).isEqualTo("Name");

        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudentId);
    }

    @Test
    public void testGetStudentInfoById_whenStudentExists() throws Exception {
        long savedStudentId = postStudentAndGetStudentsId("Name", 12);

        ResponseEntity<StudentDtoResponse> findResponse = restTemplate
                .getForEntity("http://localhost:" + port + "/student/" + savedStudentId, StudentDtoResponse.class);
        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(findResponse.getBody().getId()).isEqualTo(savedStudentId);
        Assertions.assertThat(findResponse.getBody().getAge()).isEqualTo(12);
        Assertions.assertThat(findResponse.getBody().getName()).isEqualTo("Name");

        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudentId);
    }

    @Test
    public void testGetStudentInfoById_whenStudentDoesNotExist() throws Exception {
        ResponseEntity<StudentDtoResponse> findResponse = restTemplate
                .getForEntity("http://localhost:" + port + "/student/" + 16, StudentDtoResponse.class);
        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetAllStudents() throws Exception {
        long savedStudent1Id = postStudentAndGetStudentsId("Name", 7);
        long savedStudent2Id = postStudentAndGetStudentsId("Name", 10);
        long savedStudent3Id = postStudentAndGetStudentsId("Name", 10);
        long savedStudent4Id = postStudentAndGetStudentsId("Name", 17);

        int totalStudents = 4;

        ResponseEntity<ArrayList<StudentDtoResponse>> findResponse = restTemplate
                .exchange(
                        "http://localhost:" + port + "/student",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );

        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(findResponse.getBody().size()).isEqualTo(totalStudents);

        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudent1Id);
        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudent2Id);
        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudent3Id);
        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudent4Id);
    }

    @Test
    public void testGetStudentsByAgeBetween() throws Exception {
        long savedStudent1Id = postStudentAndGetStudentsId("Name", 6);
        long savedStudent2Id = postStudentAndGetStudentsId("Name", 8);
        long savedStudent3Id = postStudentAndGetStudentsId("Name", 10);
        long savedStudent4Id = postStudentAndGetStudentsId("Name", 27);

        int minAge = 7;
        int maxAge = 11;

        ResponseEntity<ArrayList<StudentDtoResponse>> findResponse = restTemplate
                .exchange(
                        "http://localhost:" + port + "/student/filter?minAge=" + minAge + "&maxAge=" + maxAge,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );

        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(findResponse.getBody().size()).isEqualTo(2);

        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudent1Id);
        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudent2Id);
        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudent3Id);
        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudent4Id);
    }

    @Test
    public void testGetStudentsByAge() throws Exception {
        int testedAge = 10;
        int numberOfStudentsForTest = 2;

        long savedStudent1Id = postStudentAndGetStudentsId("Name", 6);
        long savedStudent2Id = postStudentAndGetStudentsId("Name", 10);
        long savedStudent3Id = postStudentAndGetStudentsId("Name", 10);
        long savedStudent4Id = postStudentAndGetStudentsId("Name", 27);

        ResponseEntity<ArrayList<StudentDtoResponse>> findResponse = restTemplate
                .exchange(
                        "http://localhost:" + port + "/student/filter/" + testedAge,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );

        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(findResponse.getBody().size()).isEqualTo(numberOfStudentsForTest);

        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudent1Id);
        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudent2Id);
        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudent3Id);
        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudent4Id);
    }

    @Test
    public void testEditStudent_whenStudentExists() throws Exception {
        long savedStudentId = postStudentAndGetStudentsId("Name", 12);

        ResponseEntity<StudentDtoResponse> findResponse = restTemplate
                .getForEntity("http://localhost:" + port + "/student/" + savedStudentId, StudentDtoResponse.class);
        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        StudentDtoResponse studentAfterEditing = new StudentDtoResponse();
        studentAfterEditing.setName("New Name");
        studentAfterEditing.setAge(15);
        studentAfterEditing.setId(savedStudentId);

        restTemplate.put("http://localhost:" + port + "/student", studentAfterEditing);

        ResponseEntity<StudentDtoResponse> response = restTemplate
                .getForEntity("http://localhost:" + port + "/student/" + savedStudentId, StudentDtoResponse.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(response.getBody().getAge()).isEqualTo(15);
        Assertions.assertThat(response.getBody().getName()).isEqualTo("New Name");

        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudentId);
    }

    @Test
    public void testEditStudent_whenStudentDoesNotExist() throws Exception {
        StudentDtoResponse studentAfterEditing = new StudentDtoResponse();
        studentAfterEditing.setName("New Name");
        studentAfterEditing.setAge(15);
        studentAfterEditing.setId(16);

        RequestEntity<StudentDtoResponse> requestEntity = RequestEntity
                .put("http://localhost:" + port + "/student")
                .body(studentAfterEditing);
        ResponseEntity<StudentDtoResponse> response = restTemplate.exchange(
                requestEntity,
                StudentDtoResponse.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testDeleteStudent_whenStudentExists() throws Exception {
        long savedStudentId = postStudentAndGetStudentsId("Name", 12);

        ResponseEntity<StudentDtoResponse> findResponse = restTemplate
                .getForEntity("http://localhost:" + port + "/student/" + savedStudentId, StudentDtoResponse.class);
        Assertions.assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudentId);

        ResponseEntity<StudentDtoResponse> response = restTemplate
                .getForEntity("http://localhost:" + port + "/student/" + savedStudentId, StudentDtoResponse.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteStudent_whenStudentDoesNotExist() throws Exception {
        RequestEntity<Void> requestEntity = RequestEntity
                .delete("http://localhost:" + port + "/student/16").build();
        ResponseEntity<StudentDtoResponse> response = restTemplate.exchange(
                requestEntity,
                StudentDtoResponse.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testGetFacultyFromStudent() throws Exception {
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

        ResponseEntity<FacultyDtoResponse> findResponse = restTemplate
                .exchange(
                        "http://localhost:" + port + "/student/faculty/" + savedStudentId,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
        Assertions.assertThat(findResponse.getBody().getId()).isEqualTo(savedFacultyId);

        restTemplate.delete("http://localhost:" + port + "/student/" + savedStudentId);
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
