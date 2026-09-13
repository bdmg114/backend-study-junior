package com.gdgku.library;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * [영속성 부재 문제] LibraryController는 계층 분리, DTO 분리, 입력 검증은 잘 되어 있지만
 * Book/Loan을 전부 메모리(Map, List)에만 저장한다. 이것 때문에 생기는 문제는
 * "서버 재시작하면 사라진다" 하나가 아니다.
 *
 * - 트랜잭션이 없어서 대출 도중 일부만 성공하면 재고가 영구히 깨진 채로 남는다.
 * - 실제로는 어떤 데이터베이스 테이블에도 기록이 남지 않는다.
 *
 * 아래 실패하는 테스트들을 통과시키려면 Book/Loan을 JPA @Entity + Repository로 옮기고,
 * borrow()를 @Transactional로 감싸서 중간에 실패하면 전체가 롤백되도록 고쳐야 한다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class LibraryControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    private BookResponse registerBook(String title, int totalCopies) {
        BookRequest request = new BookRequest();
        request.setTitle(title);
        request.setTotalCopies(totalCopies);
        return restTemplate.postForEntity("/books", request, BookResponse.class).getBody();
    }

    private BookResponse getBook(Long bookId) {
        return restTemplate.getForEntity("/books/" + bookId, BookResponse.class).getBody();
    }

    private ResponseEntity<LoanResponse> borrow(Long bookId, String borrowerName) {
        return restTemplate.postForEntity("/loans", new LoanRequest(bookId, borrowerName), LoanResponse.class);
    }

    @Test
    void 책을_대출하면_재고가_줄어들고_대출_목록에_추가된다() {
        BookResponse book = registerBook("클린 코드", 2);

        ResponseEntity<LoanResponse> response = borrow(book.getId(), "Alice");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, getBook(book.getId()).getAvailableCopies());
    }

    @Test
    void 반납하면_재고가_다시_늘어난다() {
        BookResponse book = registerBook("이펙티브 자바", 1);
        LoanResponse loan = borrow(book.getId(), "Bob").getBody();

        restTemplate.exchange("/loans/" + loan.getId() + "/return", HttpMethod.PATCH, null, LoanResponse.class);

        assertEquals(1, getBook(book.getId()).getAvailableCopies());
    }

    @Test
    void 대출_도중_실패해도_재고는_원상복구되어야_한다() {
        BookResponse book = registerBook("클린 아키텍처", 2);
        borrow(book.getId(), "Alice"); // 정상 대출: 재고 2 -> 1

        // Alice가 같은 책을 또 빌리려고 시도한다 (이미 대출 중이라 거부되어야 하는 요청).
        // 검증(@NotBlank 등)은 통과하는 값이라, Service의 뒤늦은 중복 검사에서만 걸러진다.
        restTemplate.postForEntity("/loans", new LoanRequest(book.getId(), "Alice"), String.class);

        assertEquals(1, getBook(book.getId()).getAvailableCopies(),
                "이미 대출 중이라 거부됐어야 할 요청 때문에 재고가 잘못 차감된 채로 남아 있습니다. "
                        + "트랜잭션 없이 여러 단계를 순서대로 수정해서 생긴 데이터 정합성 문제입니다.");
    }

    @Test
    void 도서와_대출_기록은_데이터베이스에_영속화되어_있어야_한다() throws SQLException {
        registerBook("클린 코드", 1);

        try (Connection connection = dataSource.getConnection()) {
            try (ResultSet bookTable = connection.getMetaData().getTables(null, null, "BOOK", new String[]{"TABLE"})) {
                assertTrue(bookTable.next(),
                        "BOOK 테이블이 데이터베이스에 없습니다. Book을 JPA @Entity로 만들어 저장하도록 고치세요.");
            }
            try (ResultSet loanTable = connection.getMetaData().getTables(null, null, "LOAN", new String[]{"TABLE"})) {
                assertTrue(loanTable.next(),
                        "LOAN 테이블이 데이터베이스에 없습니다. Loan을 JPA @Entity로 만들어 저장하도록 고치세요.");
            }
        }
    }
}
