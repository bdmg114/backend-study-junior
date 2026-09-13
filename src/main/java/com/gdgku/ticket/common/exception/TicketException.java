package com.gdgku.ticket.common.exception;

public class TicketException extends RuntimeException {

    private final ErrorCode errorCode;

    public TicketException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
