package learn.jpa.repository;

import java.util.Optional;
import javax.persistence.LockModeType;
import learn.jpa.domain.board.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<Post> findPostById(Long id);
}
