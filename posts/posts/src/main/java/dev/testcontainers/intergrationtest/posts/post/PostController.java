package dev.testcontainers.intergrationtest.posts.post;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts/")
public class PostController {

    private static  final Logger log = LoggerFactory.getLogger(PostController.class);
    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("")
    List<Post> getAllPosts() {
        log.info("Fetching all posts");
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Integer id) {
        log.info("Fetching post with id: {}", id);
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        return ResponseEntity.ok(post);
    }


    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Post savePost(@RequestBody @Valid Post post) {
        log.info("Saving post: {}", post);
        return postRepository.save(post);
    }

    @PutMapping("/{id}")
    Post update(@PathVariable Integer id, @RequestBody @Valid Post post) {
        log.info("Updating post with id: {}", id);
        Optional<Post> existingPostOptional = postRepository.findById(id);
        if(existingPostOptional.isPresent()) {
            Post updatedPost = new Post(
                    existingPostOptional.get().id(),
                    existingPostOptional.get().userId(),
                    post.title(), post.body(),
                    existingPostOptional.get().version()
            );
            return postRepository.save(updatedPost);
        } else {
            throw new PostNotFoundException();
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void deletePost(@PathVariable Integer id) {
        log.info("Deleting post with id: {}", id);
        postRepository.deleteById(id);
    }
}
