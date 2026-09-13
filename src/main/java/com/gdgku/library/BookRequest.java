package com.gdgku.library;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BookRequest {

    @NotBlank(message = "도서명은 필수입니다.")
    private String title;

    @NotNull(message = "총 권수는 필수입니다.")
    @Positive(message = "총 권수는 1권 이상이어야 합니다.")
    private Integer totalCopies;

    public BookRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
    }
}
