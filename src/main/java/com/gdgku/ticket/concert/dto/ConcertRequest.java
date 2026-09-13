package com.gdgku.ticket.concert.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class ConcertRequest {

    @NotBlank(message = "공연 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "아티스트명은 필수입니다.")
    private String artist;

    @NotBlank(message = "공연장은 필수입니다.")
    private String venue;

    @NotNull(message = "공연 일시는 필수입니다.")
    @Future(message = "공연 일시는 미래 시각이어야 합니다.")
    private LocalDateTime concertAt;

    @NotNull(message = "총 좌석 수는 필수입니다.")
    @Positive(message = "총 좌석 수는 1석 이상이어야 합니다.")
    private Integer totalSeats;

    @NotNull(message = "가격은 필수입니다.")
    @Positive(message = "가격은 0보다 커야 합니다.")
    private Integer price;

    public ConcertRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDateTime getConcertAt() {
        return concertAt;
    }

    public void setConcertAt(LocalDateTime concertAt) {
        this.concertAt = concertAt;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
