package dev.testcontainers.intergrationtest.posts.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    PostRepository postRepository;

    @Test
    void connectionEstablished() {
        // This test will pass if the connection to the PostgreSQL container is established successfully
        // You can add assertions here to verify the connection or perform any other operations
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void setUp() {
        // This method will be called before each test
        // You can use it to set up any necessary data or state for your tests
        List<Post> posts = List.of(
                new Post(1, 1, "This is my first post", "This is the body of my first post", null),
                new Post(2, 2, "This is my second post", "This is the body of my second post", null),
                new Post(3, 3, "This is my third post", "This is the body of my third post", null)
        );
        postRepository.saveAll(posts);
    }

    @Test
    void shouldReturnPostByTitle() {
        // Given
        String title = "This is my first post";
        // When
        Post post = postRepository.findByTitle(title);
        // Then
        assertThat(post).isNotNull();
    }
}
