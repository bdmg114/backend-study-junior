package com.gdgku.library;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * [문제: 영속성 부재]
 *
 * 계층 분리, DTO 분리, 입력 검증은 잘 되어 있지만 Book/Loan을 전부 메모리(Map, List)에만
 * 저장한다. 이것 때문에 생기는 문제는 "서버 재시작하면 사라진다" 하나가 아니다.
 * - 트랜잭션이 없어서, borrow() 도중 일부만 성공하면(재고는 깎였는데 대출 기록은 안 남는 등)
 *   데이터가 영구히 깨진 상태로 남는다.
 * - 실제로는 어떤 데이터베이스 테이블에도 기록이 남지 않는다 — 서버 프로세스가 죽으면 그걸로 끝이다.
 *
 * LibraryControllerTest의 실패하는 테스트들이 이 문제들을 각각 잡아낸다.
 *
 * 할 일: Book/Loan을 JPA @Entity로 만들고 각각의 Repository(JpaRepository)로 옮긴 뒤,
 * borrow()를 @Transactional로 감싸서 중간에 실패하면 전체가 롤백되도록 고치자.
 */
@RestController
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/books")
    public BookResponse registerBook(@Valid @RequestBody BookRequest request) {
        Book book = libraryService.registerBook(request.getTitle(), request.getTotalCopies());
        return new BookResponse(book);
    }

    @GetMapping("/books/{bookId}")
    public BookResponse getBook(@PathVariable Long bookId) {
        return new BookResponse(libraryService.getBook(bookId));
    }

    @PostMapping("/loans")
    public LoanResponse borrow(@Valid @RequestBody LoanRequest request) {
        Loan loan = libraryService.borrow(request.getBookId(), request.getBorrowerName());
        return new LoanResponse(loan);
    }

    @GetMapping("/loans")
    public List<LoanResponse> getAllLoans() {
        return libraryService.getAllLoans().stream().map(LoanResponse::new).toList();
    }

    @PatchMapping("/loans/{loanId}/return")
    public LoanResponse returnBook(@PathVariable Long loanId) {
        return new LoanResponse(libraryService.returnBook(loanId));
    }
}
