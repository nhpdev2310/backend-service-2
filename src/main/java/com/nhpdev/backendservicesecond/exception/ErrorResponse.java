package com.nhpdev.backendservicesecond.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String code;
    private int status;
    private String message;
    private String error;
    private String path;
    private Instant timeStamp;
}
