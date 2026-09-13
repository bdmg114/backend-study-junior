package com.gdgku.library;

public class Loan {

    private Long id;
    private Long bookId;
    private String borrowerName;
    private boolean returned;

    public Loan() {
    }

    public Loan(Long id, Long bookId, String borrowerName, boolean returned) {
        this.id = id;
        this.bookId = bookId;
        this.borrowerName = borrowerName;
        this.returned = returned;
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
