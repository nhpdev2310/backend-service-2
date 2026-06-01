package com.nhpdev.backendservicesecond.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nhpdev.backendservicesecond.dto.internal.ErrorDetail;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String code;
    private String message;
    private String error;
    private String path;
    private Instant timeStamp;
    private List<ErrorDetail> errors;

    public static ErrorResponse of (ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .error(errorCode.getStatus().getReasonPhrase())
                .message(errorCode.getMessage())
                .timeStamp(Instant.now())
                .path(path)
                .build();
    }
}
