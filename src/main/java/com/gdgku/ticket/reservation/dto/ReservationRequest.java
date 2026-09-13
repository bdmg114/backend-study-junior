package com.gdgku.ticket.reservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReservationRequest {

    @NotBlank(message = "예매자 이름은 필수입니다.")
    private String reserverName;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String reserverEmail;

    @NotNull(message = "예매 좌석 수는 필수입니다.")
    @Positive(message = "예매 좌석 수는 1석 이상이어야 합니다.")
    private Integer seatCount;

    public ReservationRequest() {
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

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }
}
