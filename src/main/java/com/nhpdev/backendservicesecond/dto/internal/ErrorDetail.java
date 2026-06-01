package com.nhpdev.backendservicesecond.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ErrorDetail {
    private String field;
    private String code;
    private String message;
}
