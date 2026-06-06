package com.nhpdev.backendservicesecond.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Auth
    UNAUTHENTICATED("AUTH_001", HttpStatus.UNAUTHORIZED, "Authentication required"),
    FORBIDDEN("AUTH_002", HttpStatus.FORBIDDEN, "Access denied"),
    TOKEN_EXPIRED("AUTH_003", HttpStatus.UNAUTHORIZED, "Token has expired"),
    TOKEN_INVALID("AUTH_004", HttpStatus.UNAUTHORIZED, "Token is invalid"),
    UNSUPPORTED_TOKEN_TYPE("TOKEN_005", HttpStatus.UNAUTHORIZED, "Unsupported token type"),
    TOKEN_VERIFICATION_FAILED("TOKEN_006", HttpStatus.UNAUTHORIZED, "Token verification failed"),
    REFRESH_TOKEN_EXPIRED("AUTH_007", HttpStatus.UNAUTHORIZED, "Refresh token has expired"),
    TOKEN_GENERATION_FAILED("AUTH_008", HttpStatus.UNAUTHORIZED, "Token generate Failed"),
    TOKEN_MISSING("AUTH_009", HttpStatus.UNAUTHORIZED, "Token missing"),
    TOKEN_PARSE_FAILED("AUTH_010", HttpStatus.UNAUTHORIZED, "Token parse failed"),

    // User
    USER_NOT_FOUND("USER_001", HttpStatus.NOT_FOUND, "User not found"),
    USER_ALREADY_EXISTS("USER_002", HttpStatus.CONFLICT, "User already exists"),
    USER_DISABLED("USER_003", HttpStatus.FORBIDDEN, "User account is disabled"),
    USER_BANNED("USER_004", HttpStatus.FORBIDDEN, "User account is banned"),
    INVALID_CREDENTIALS("USER_005", HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    EMAIL_NOT_VERIFIED("USER_006", HttpStatus.FORBIDDEN, "Email not verified"),
    USER_ALREADY_ACTIVATED("USER_007", HttpStatus.BAD_REQUEST, "User is already activated"),

    // Role
    ROLE_NOT_FOUND("ROLE_001", HttpStatus.NOT_FOUND, "Role not found"),
    ROLE_ALREADY_EXISTS("ROLE_002", HttpStatus.CONFLICT, "Role already exists"),

    // Permission
    PERMISSION_NOT_FOUND("PERM_001", HttpStatus.NOT_FOUND, "Permission not found"),
    PERMISSION_ALREADY_EXISTS("PERM_002", HttpStatus.CONFLICT, "Permission already exists"),

    // Validation
    INVALID_INPUT("VAL_001", HttpStatus.BAD_REQUEST, "Invalid input"),
    MISSING_FIELD("VAL_002", HttpStatus.BAD_REQUEST, "Missing required field"),
    INVALID_FORMAT("VAL_003", HttpStatus.BAD_REQUEST, "Invalid format"),
    INVALID_EMAIL("VAL_004", HttpStatus.BAD_REQUEST, "Invalid email"),
    INVALID_PASSWORD("VAL_005", HttpStatus.BAD_REQUEST, "Invalid password"),

    // Media
    UPLOAD_FILE_FAILED("SYS_005", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file"),

    // System
    INTERNAL_SERVER_ERROR("SYS_001", HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    SERVICE_UNAVAILABLE("SYS_002", HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable"),
    DATABASE_ERROR("SYS_003", HttpStatus.INTERNAL_SERVER_ERROR, "Database error"),
    METHOD_NOT_ALLOWED("SYS_004", HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not supported"),
    MISSING_PARAMETER("VAL_006", HttpStatus.BAD_REQUEST, "Required parameter is missing"),
    MISSING_HEADER("VAL_007", HttpStatus.BAD_REQUEST, "Required header is missing"),
    MISSING_COOKIE("VAL_008", HttpStatus.BAD_REQUEST, "Required cookie is missing"),
    DATA_INTEGRITY_VIOLATION("DB_001", HttpStatus.BAD_REQUEST, "Data integrity violation")

    ;
    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}