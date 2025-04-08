package dev.testcontainers.intergrationtest.posts.post;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers //This annotation is used to indicate that the test class will use Testcontainers. It will start the containers before the tests and stop them after the tests are done.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //Because it is a Spring Boot test, it will automatically load the application context and all the beans (IntergrationTest)
@Transactional //This annotation is used to indicate that the test class will use transactions. It will roll back the transactions after each test method, so the database will be in the same state before and after the tests.
public class PostControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    PostRepository postRepository;


    @Test
    @Rollback
    void shouldCreateNewPostWhenPostIsValid() {
        // Given
        String url = "/api/posts/";
        Post post = new Post(2000, 1, "This is my first post", "This is the body of my first post", null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Post> request = new HttpEntity<>(post, headers);

        // When
        ResponseEntity<Post> response = testRestTemplate.exchange(url, HttpMethod.POST, request, Post.class);

        // Then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().id()).isEqualTo(2000);
        Assertions.assertThat(response.getBody().userId()).isEqualTo(1);
        Assertions.assertThat(response.getBody().title()).isEqualTo("This is my first post");
        Assertions.assertThat(response.getBody().body()).isEqualTo("This is the body of my first post");
    }


    @Test
    void shouldFindAllPosts() {
        // Given
        String url = "/api/posts/";
        // When
        Post[] posts = testRestTemplate.getForObject(url, Post[].class);
        // Then
        assertThat(posts.length).isGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldFindPostWhenValidPostID() {
        // Given: créer un post via l'API
        Post post = new Post(101, 1, "This is my first post", "This is the body of my first post", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Post> request = new HttpEntity<>(post, headers);

        ResponseEntity<Post> postResponse = testRestTemplate.postForEntity("/api/posts/", request, Post.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // When: récupérer le post par son ID
        ResponseEntity<Post> response = testRestTemplate.exchange("/api/posts/101", HttpMethod.GET, null, Post.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }



    @Test
    void shouldFindPostWhenInvalidPostID() {
        // Given
        String url = "/api/posts/100";
        // When
        var exchange = testRestTemplate.exchange(url, HttpMethod.GET, null, Post.class);
        ResponseEntity<Post> response = exchange;
        // Then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldThrowNotFoundWhenInvalidPostID() {
        // Given
        String url = "/api/posts/100";
        // When
        var exchange = testRestTemplate.exchange(url, HttpMethod.GET, null, Post.class);
        ResponseEntity<Post> response = exchange;
        // Then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotCreateNewPostWhenValidationFails() {
        // Given
        String url = "/api/posts/";
        Post post = new Post(12, 12, "", "", null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Post> request = new HttpEntity<>(post, headers);

        // When
        ResponseEntity<Post> response = testRestTemplate.exchange(url, HttpMethod.POST, request, Post.class);

        // Then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    @Rollback
    void shouldDeleteWithValidPostID() {
        // Given: créer un post via l'API
        Post post = new Post(101, 101, "This is my first post", "This is the body of my first post", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Post> request = new HttpEntity<>(post, headers);

        ResponseEntity<Post> postResponse = testRestTemplate.postForEntity("/api/posts/", request, Post.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // When: supprimer le post par son ID
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/posts/101", HttpMethod.DELETE, null, Void.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }


}
