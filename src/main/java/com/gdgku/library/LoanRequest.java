package com.gdgku.library;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoanRequest {

    @NotNull(message = "도서 ID는 필수입니다.")
    private Long bookId;

    @NotBlank(message = "대출자 이름은 필수입니다.")
    private String borrowerName;

    public LoanRequest() {
    }

    public LoanRequest(Long bookId, String borrowerName) {
        this.bookId = bookId;
        this.borrowerName = borrowerName;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }
}
