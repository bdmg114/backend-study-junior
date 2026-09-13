package com.gdgku.library;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LibraryService {

    private final Map<Long, Book> books = new HashMap<>();
    private final List<Loan> loans = new ArrayList<>();
    private long nextBookId = 1L;
    private long nextLoanId = 1L;

    public Book registerBook(String title, int totalCopies) {
        Book book = new Book(nextBookId++, title, totalCopies, totalCopies);
        books.put(book.getId(), book);
        return book;
    }

    public Book getBook(Long bookId) {
        return books.get(bookId);
    }

    public Loan borrow(Long bookId, String borrowerName) {
        Book book = books.get(bookId);
        if (book == null) {
            throw new IllegalArgumentException("존재하지 않는 도서입니다.");
        }
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("대출 가능한 재고가 없습니다.");
        }

        // 1단계: 재고 차감. 트랜잭션이 없어서 2단계가 실패해도 이 변경은 롤백되지 않는다.
        book.setAvailableCopies(book.getAvailableCopies() - 1);

        // 2단계: 이미 같은 책을 빌리고 아직 반납하지 않았는지는 재고를 깎은 "뒤에" 확인한다.
        boolean alreadyBorrowing = loans.stream()
                .anyMatch(loan -> loan.getBookId().equals(bookId)
                        && loan.getBorrowerName().equals(borrowerName)
                        && !loan.isReturned());
        if (alreadyBorrowing) {
            throw new IllegalStateException(borrowerName + "님은 이미 이 책을 대출 중입니다.");
        }

        Loan loan = new Loan(nextLoanId++, bookId, borrowerName, false);
        loans.add(loan);
        return loan;
    }

    public List<Loan> getAllLoans() {
        return loans;
    }

    public Loan returnBook(Long loanId) {
        for (Loan loan : loans) {
            if (loan.getId().equals(loanId)) {
                loan.setReturned(true);
                Book book = books.get(loan.getBookId());
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                return loan;
            }
        }
        return null;
    }
}
