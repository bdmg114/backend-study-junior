package com.gdgku.ticket.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 공연입니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예매입니다."),
    NOT_ENOUGH_SEATS(HttpStatus.CONFLICT, "예매 가능한 좌석 수가 부족합니다."),
    CONCERT_HAS_RESERVATIONS(HttpStatus.CONFLICT, "이미 예매 내역이 있는 공연은 삭제할 수 없습니다."),
    RESERVATION_ALREADY_CANCELLED(HttpStatus.CONFLICT, "이미 취소된 예매입니다."),
    INVALID_TOTAL_SEATS(HttpStatus.BAD_REQUEST, "총 좌석 수는 이미 예매된 좌석 수보다 적을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
