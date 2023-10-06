package learn.jpa.service;

import javax.persistence.EntityNotFoundException;
import learn.jpa.domain.board.Post;
import learn.jpa.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public Post savePost(String title) {
        Post post = new Post();
        post.setName(title);
        post.setViews(0L);
        return postRepository.save(post);
    }

    // PESSIMISTIC_WRITE
    @Transactional
    public Post getPostPessimistic(Long id) {
        Post post = postRepository.findPostById(id).orElseThrow(EntityNotFoundException::new);
        post.increaseViews();
        return post;
    }

    @Transactional
    public Post getPost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(EntityNotFoundException::new);
        post.increaseViews();
        return post;
    }
}
