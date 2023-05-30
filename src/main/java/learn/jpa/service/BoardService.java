package learn.jpa.service;

import javax.persistence.EntityNotFoundException;
import learn.jpa.domain.board.Board;
import learn.jpa.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    @Transactional
    public Board saveBoard(String title) {
        Board board = new Board();
        board.setName(title);
        board.setViews(0L);
        return boardRepository.save(board);
    }

    @Transactional
    public Board getBoardPessimistic(Long id) {
        Board boardById = boardRepository.findBoardById(id).orElseThrow(EntityNotFoundException::new);
        boardById.increaseViews();
        return boardById;
    }

    @Transactional
    public Board getBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        board.increaseViews();
        return board;
    }
}
