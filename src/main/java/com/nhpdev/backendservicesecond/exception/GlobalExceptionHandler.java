package com.nhpdev.backendservicesecond.exception;

import com.nhpdev.backendservicesecond.dto.internal.ErrorDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom business exceptions thrown by the backend service.
     * Maps the error code and message directly from the ErrorCode enum.
     */
    @ExceptionHandler(BackendServiceException.class)
    public ResponseEntity<ErrorResponse> handleBackendServiceException(BackendServiceException e, WebRequest request) {
        ErrorCode errorCode = e.getErrorCode();
        return buildErrorResponse(errorCode, errorCode.getMessage(), null, request);
    }

    /**
     * Handles authentication failures due to invalid credentials (e.g., wrong email or password).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e, WebRequest request) {
        return buildErrorResponse(ErrorCode.INVALID_CREDENTIALS, ErrorCode.INVALID_CREDENTIALS.getMessage(), null, request);
    }

    /**
     * Handles access denied exceptions when a user lacks the necessary permissions for an action.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e, WebRequest request) {
        return buildErrorResponse(ErrorCode.FORBIDDEN, ErrorCode.FORBIDDEN.getMessage(), null, request);
    }

//    org.springframework.security.authentication.DisabledException:
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handlerDisabledException(DisabledException e, WebRequest request) {
        return buildErrorResponse(ErrorCode.USER_DISABLED, ErrorCode.USER_DISABLED.getMessage(), null, request);
    }

    /**
     * Handles validation errors for request DTOs.
     * Sorts errors by priority (e.g., MISSING_FIELD before INVALID_FORMAT)
     * and ensures only the most critical error per field is returned.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e,
                                                                   WebRequest request) {
        List<ErrorDetail> errorDetails = e.getBindingResult()
                .getFieldErrors().stream()
                .sorted(Comparator.comparingInt(this::getErrorCodePriority))
                .collect(Collectors.toMap(
                        FieldError::getField,
                        this::mapToErrorDetail,
                        (existing, duplicate) -> existing,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .toList();
        ErrorResponse response = ErrorResponse.builder()
                .code(ErrorCode.INVALID_INPUT.getCode())
                .message("Validation Error")
                .path(request.getDescription(false).replace("uri=", ""))
                .timeStamp(Instant.now())
                .errors(errorDetails)
                .build();
        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getStatus()).body(response);
    }

    /**
     * Handles exceptions when a required request parameter is missing.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, WebRequest request) {
        return buildErrorResponse(ErrorCode.MISSING_PARAMETER, e.getMessage(), null, request);
    }

    /**
     * Handles exceptions when a required request header is missing.
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException e, WebRequest request) {
        return buildErrorResponse(ErrorCode.MISSING_HEADER, "Required header '" + e.getHeaderName() + "' is missing", null, request);
    }

    /**
     * Handles exceptions when a required request cookie is missing.
     */
    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestCookieException(MissingRequestCookieException e, WebRequest request) {
        return buildErrorResponse(ErrorCode.MISSING_COOKIE, "Required cookie '" + e.getCookieName() + "' is missing", null, request);
    }

    /**
     * Handles exceptions when an unsupported HTTP method is used for an endpoint.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, WebRequest request) {
        return buildErrorResponse(ErrorCode.METHOD_NOT_ALLOWED, e.getMessage(), null, request);
    }

    /**
     * Handles database-related errors, such as unique constraint violations.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e, WebRequest request) {
        return buildErrorResponse(ErrorCode.DATA_INTEGRITY_VIOLATION, e.getMostSpecificCause().getMessage(), null, request);
    }

    /**
     * Handles malformed JSON requests or incompatible data formats.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, WebRequest request) {
        return buildErrorResponse(ErrorCode.INVALID_FORMAT, "Malformed JSON request", "JSON Parse Error", request);
    }

    /**
     * Catch-all handler for any other unhandled exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e, WebRequest request) {
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), e.getClass().getSimpleName(), request);
    }

    /**
     * Utility method to build a standardized ErrorResponse object.
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode, String message, String errorName, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(message != null ? message : errorCode.getMessage())
                .error(errorName != null ? errorName : errorCode.getStatus().getReasonPhrase())
                .path(request.getDescription(false).replace("uri=", ""))
                .timeStamp(Instant.now())
                .build();
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    /**
     * Maps a Spring FieldError to a custom ErrorDetail object.
     */
    private ErrorDetail mapToErrorDetail(FieldError fieldError) {
        String enumkey = fieldError.getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        try {
            errorCode = ErrorCode.valueOf(enumkey);
        } catch (IllegalArgumentException ex) {
            log.info("Code {} not found, fallback to default", enumkey);
        }
        return ErrorDetail.builder()
                .code(errorCode.getCode())
                .field(fieldError.getField())
                .message(errorCode.getMessage())
                .build();
    }

    /**
     * Determines the priority of an error based on its definition order in the ErrorCode enum.
     */
    private int getErrorCodePriority(FieldError fieldError) {
        try {
            return ErrorCode.valueOf(fieldError.getDefaultMessage()).ordinal();
        } catch (Exception ex) {
            return Integer.MAX_VALUE;
        }
    }
}
