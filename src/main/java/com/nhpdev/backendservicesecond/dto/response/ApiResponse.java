package com.nhpdev.backendservicesecond.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T> {
    String message;
    T data;
    int status;
    Instant timestamp;

    public boolean isOk() {
        return status >= 200 && status < 300;
    }

    // GET — 200
    public static <T> ApiResponse<T> success(T data) {
        return success(data, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .data(data)
                .status(HttpStatus.OK.value())
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    // POST — 201
    public static <T> ApiResponse<T> created(T data) {
        return created(data, null);
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .data(data)
                .status(HttpStatus.CREATED.value())
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    // DELETE, PUT không có data — 204
    public static <T> ApiResponse<T> noContent() {
        return ApiResponse.<T>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .timestamp(Instant.now())
                .build();
    }

    // Error
    public static <T> ApiResponse<T> error(String message, int status) {
        return ApiResponse.<T>builder()
                .message(message)
                .status(status)
                .timestamp(Instant.now())
                .build();
    }
}
