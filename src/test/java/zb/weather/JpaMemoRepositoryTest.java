package zb.weather;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zb.weather.domain.Memo;
import zb.weather.repository.JpaMemoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class JpaMemoRepositoryTest {

    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    @DisplayName("Memo 저장 성공")
    void saveMemoTest() {
        // given
        Memo newMemo = new Memo(null, "This is Wednesday");

        // when
        Memo savedMemo = jpaMemoRepository.save(newMemo);

        // then
        assertNotNull(savedMemo);
        assertEquals(newMemo.getText(), savedMemo.getText());
    }

    @Test
    @DisplayName("Memo 아이디로 조회 테스트")
    void findByIdTest() {
        // given
        Memo newMemo = new Memo(1, "This is Wednesday");
        Memo savedMemo = jpaMemoRepository.save(newMemo);
        int savedMemoId = savedMemo.getId();

        // when
        Optional<Memo> foundMemo = jpaMemoRepository.findById(savedMemoId);

        // then
        assertTrue(foundMemo.isPresent());
        assertEquals(savedMemoId, foundMemo.get().getId());
        assertEquals("This is Wednesday", foundMemo.get().getText());
    }

    @Test
    @DisplayName("Memo 아이디로 조회 - 존재하지 않는 아이디")
    void findByIdNotFoundTest() {
        // when
        Optional<Memo> foundMemo = jpaMemoRepository.findById(999);

        // then
        assertFalse(foundMemo.isPresent());
    }

    @Test
    @DisplayName("모든 Memo 조회 테스트")
    void findAllTest() {
        // given
        Memo memo1 = new Memo(1, "Memo One");
        Memo memo2 = new Memo(2, "Memo Two");
        jpaMemoRepository.save(memo1);
        jpaMemoRepository.save(memo2);

        // when
        List<Memo> memoList = jpaMemoRepository.findAll();

        // then
        assertNotNull(memoList);
        assertEquals(2, memoList.size());
        assertTrue(memoList.stream().anyMatch(memo -> memo.getText().equals("Memo One")));
        assertTrue(memoList.stream().anyMatch(memo -> memo.getText().equals("Memo Two")));
    }
}
