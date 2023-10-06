package learn.jpa.repository;

import java.util.Optional;
import javax.persistence.LockModeType;
import learn.jpa.domain.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<Board> findBoardById(Long id);
}
