package com.gdgku.ticket.concert.dto;

import com.gdgku.ticket.concert.Concert;

import java.time.LocalDateTime;

public class ConcertResponse {

    private Long id;
    private String title;
    private String artist;
    private String venue;
    private LocalDateTime concertAt;
    private int totalSeats;
    private int reservedSeats;
    private int availableSeats;
    private int price;

    public ConcertResponse() {
    }

    public ConcertResponse(Concert concert) {
        this.id = concert.getId();
        this.title = concert.getTitle();
        this.artist = concert.getArtist();
        this.venue = concert.getVenue();
        this.concertAt = concert.getConcertAt();
        this.totalSeats = concert.getTotalSeats();
        this.reservedSeats = concert.getReservedSeats();
        this.availableSeats = concert.getAvailableSeats();
        this.price = concert.getPrice();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(int reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
