package com.gdgku.library;

public class LoanResponse {

    private Long id;
    private Long bookId;
    private String borrowerName;
    private boolean returned;

    public LoanResponse() {
    }

    public LoanResponse(Loan loan) {
        this.id = loan.getId();
        this.bookId = loan.getBookId();
        this.borrowerName = loan.getBorrowerName();
        this.returned = loan.isReturned();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
