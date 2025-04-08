package dev.testcontainers.intergrationtest.posts;

import org.springframework.boot.SpringApplication;

public class TestPostsApplication {

	public static void main(String[] args) {
		SpringApplication.from(PostsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
