package com.nhpdev.backendservicesecond.exception;

import lombok.Getter;

@Getter
public class BackendServiceException extends RuntimeException {
    private final ErrorCode errorCode;

    public BackendServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
