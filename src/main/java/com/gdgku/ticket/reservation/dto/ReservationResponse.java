package com.gdgku.ticket.reservation.dto;

import com.gdgku.ticket.reservation.Reservation;
import com.gdgku.ticket.reservation.ReservationStatus;

import java.time.LocalDateTime;

public class ReservationResponse {

    private Long id;
    private Long concertId;
    private String concertTitle;
    private String reserverName;
    private String reserverEmail;
    private int seatCount;
    private ReservationStatus status;
    private LocalDateTime reservedAt;

    public ReservationResponse() {
    }

    public ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.concertId = reservation.getConcert().getId();
        this.concertTitle = reservation.getConcert().getTitle();
        this.reserverName = reservation.getReserverName();
        this.reserverEmail = reservation.getReserverEmail();
        this.seatCount = reservation.getSeatCount();
        this.status = reservation.getStatus();
        this.reservedAt = reservation.getReservedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConcertId() {
        return concertId;
    }

    public void setConcertId(Long concertId) {
        this.concertId = concertId;
    }

    public String getConcertTitle() {
        return concertTitle;
    }

    public void setConcertTitle(String concertTitle) {
        this.concertTitle = concertTitle;
    }

    public String getReserverName() {
        return reserverName;
    }

    public void setReserverName(String reserverName) {
        this.reserverName = reserverName;
    }

    public String getReserverEmail() {
        return reserverEmail;
    }

    public void setReserverEmail(String reserverEmail) {
        this.reserverEmail = reserverEmail;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
    }
}
