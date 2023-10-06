package learn.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import learn.jpa.domain.board.Board;
import learn.jpa.domain.board.Post;
import learn.jpa.repository.BoardRepository;
import learn.jpa.service.BoardService;
import learn.jpa.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@SpringBootTest
class LockTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardService boardService;

    @Autowired
    private PostService postService;

//    @Autowired
//    private PlatformTransactionManager tm;

    @Autowired
    private TransactionTemplate transaction;

    @BeforeEach
    void setUp() {
//        transaction = new TransactionTemplate(tm);
        // 트랜잭션 내부에 트랜잭션이 생기면 새로 생긴 트랜잭션으로 동작하도록 해서 쓰레드마다 동작하도록 한다.
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @DisplayName("트랜잭션의 커밋된 시점의 version의 차이를 이용하여 낙관락을 구현한다.")
    @Test
    @Transactional
    void optimisticLock() throws InterruptedException {
        int numberOfThread = 50;
        CountDownLatch latch = new CountDownLatch(numberOfThread);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThread);
        Board board = transaction.execute((b) -> boardService.saveBoard("테스트"));

        for (int i = 0; i < numberOfThread; i++) {
            executorService.execute(() -> {
                try {
                    transaction.execute((status -> {
                        Board updateBoard = boardRepository.findById(board.getId()).orElse(null);
                        updateBoard.updateTitle("제목" + UUID.randomUUID());
                        return updateBoard;
                    }));
                } catch (Exception e) {
                    // 충돌시 오류 메시지
                    System.out.println("e = " + e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        Board result = boardRepository.findById(board.getId()).orElse(null);
        System.out.println("result.getVersion() = " + result.getVersion());

        // 충돌이 발생하게되면 version은 50이 되지 않아야 한다.
        assertThat(result.getVersion()).isLessThan(numberOfThread);
    }

    @DisplayName("비관락을 적용한 후 version을 확인한다.")
    @Test
    void pessimisticTest() throws InterruptedException {
        int numberOfThread = 50;
        CountDownLatch latch = new CountDownLatch(numberOfThread);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThread);
        Board board = transaction.execute((b) -> boardService.saveBoard("테스트"));

        for (int i = 0; i < numberOfThread; i++) {
            executorService.execute(() -> {
                    transaction.execute((status -> {
                        Board updateBoard = boardRepository.findBoardById(board.getId()).orElse(null);
                        updateBoard.updateTitle("제목" + UUID.randomUUID());
                        return updateBoard;
                    }));
                    latch.countDown();
            });
        }
        latch.await();
        Board result = boardRepository.findById(board.getId()).orElse(null);

        // 비관락의 특징으로 인해 version이 50이 된다.
        assertThat(result.getVersion()).isEqualTo(numberOfThread);

    }

    @DisplayName("비관락을 걸을 때 조회수 결과")
    @Test
    void views() throws InterruptedException {
        int numberOfThread = 50;
        CountDownLatch latch = new CountDownLatch(numberOfThread);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThread);
        Post post = transaction.execute((b) -> postService.savePost("테스트"));

        for (int i = 0; i < numberOfThread; i++) {
            executorService.execute(() -> {
                try {
                    transaction.execute((status -> postService.getPostPessimistic(post.getId())));
                } catch (Exception e) {

                } finally {
                    latch.countDown();
                }

            });
        }
        latch.await();
        Post result = postService.getPostPessimistic(post.getId());
        System.out.println("result = " + result.getViews());
        assertThat(result.getViews()).isEqualTo(numberOfThread + 1);
    }

    @DisplayName("락을 걸지 않았을 때 조회수 결과")
    @Test
    void notLock() throws InterruptedException {
        int numberOfThread = 50;
        CountDownLatch latch = new CountDownLatch(numberOfThread);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThread);
        Post post = transaction.execute((b) -> postService.savePost("테스트"));

        for (int i = 0; i < numberOfThread; i++) {
            executorService.execute(() -> {
                try {
                    transaction.execute((status -> postService.getPost(post.getId())));
                } catch (Exception e) {

                } finally {
                    latch.countDown();
                }

            });

        }
        latch.await();
        Post result = postService.getPost(post.getId());
        assertThat(result.getViews()).isLessThan(numberOfThread + 1);
        System.out.println("result.getViews() = " + result.getViews());
    }
}
