package zb.weather;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zb.weather.domain.Memo;
import zb.weather.repository.JdbcMemoRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class JdbcMemoRepositoryTest {
    @Autowired
    JdbcMemoRepository jdbcMemoRepository;

    @Test
    @DisplayName("memo 저장 성공")
    void saveMemoTest() {
        // given
        Memo newMemo = new Memo(1, "Today is Thursday");

        // when
        Memo savedMemo = jdbcMemoRepository.save(newMemo);

        // then
        assertNotNull(savedMemo);
        assertEquals(1, savedMemo.getId());
        assertEquals("Today is Thursday", savedMemo.getText());
    }

    @Test
    @DisplayName("Memo 아이디로 조회 테스트")
    void findByIdTest() {
        // given
        Memo newMemo = new Memo(1, "Today is Thursday");
        jdbcMemoRepository.save(newMemo);

        // when
        Memo foundMemo = jdbcMemoRepository.findById(1);

        // then
        assertNotNull(foundMemo);
        assertEquals(1, foundMemo.getId());
        assertEquals("Today is Thursday", foundMemo.getText());
    }

    @Test
    @DisplayName("Memo 아이디로 조회 - 존재하지 않는 아이디")
    void findByIdNotFoundTest() {
        // when
        Memo foundMemo = jdbcMemoRepository.findById(999);

        // then
        assertNull(foundMemo);
    }

    @Test
    @DisplayName("모든 Memo 조회 테스트")
    void findAllTest() {
        // given
        Memo memo1 = new Memo(1, "Memo One");
        Memo memo2 = new Memo(2, "Memo Two");
        jdbcMemoRepository.save(memo1);
        jdbcMemoRepository.save(memo2);

        // when
        List<Memo> memoList = jdbcMemoRepository.findAll();

        // then
        assertNotNull(memoList);
        assertEquals(2, memoList.size());
        assertTrue(memoList.stream().anyMatch(memo -> memo.getText().equals("Memo One")));
        assertTrue(memoList.stream().anyMatch(memo -> memo.getText().equals("Memo Two")));
    }
}
