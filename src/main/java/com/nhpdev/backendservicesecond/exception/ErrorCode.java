package com.nhpdev.backendservicesecond.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Auth
    UNAUTHENTICATED(BusinessCode.AuthError.UNAUTHENTICATED, HttpStatus.UNAUTHORIZED, "Authentication required"),
    FORBIDDEN(BusinessCode.AuthError.FORBIDDEN, HttpStatus.FORBIDDEN, "Access denied"),
    TOKEN_EXPIRED(BusinessCode.AuthError.TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED, "Token has expired"),
    TOKEN_INVALID(BusinessCode.AuthError.TOKEN_INVALID, HttpStatus.UNAUTHORIZED, "Token is invalid"),
    REFRESH_TOKEN_EXPIRED(BusinessCode.AuthError.REFRESH_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED, "Refresh token has expired"),

    // User
    USER_NOT_FOUND(BusinessCode.UserError.NOT_FOUND, HttpStatus.NOT_FOUND, "User not found"),
    USER_ALREADY_EXISTS(BusinessCode.UserError.ALREADY_EXISTS, HttpStatus.CONFLICT, "User already exists"),
    USER_DISABLED(BusinessCode.UserError.DISABLED, HttpStatus.FORBIDDEN, "User account is disabled"),
    INVALID_CREDENTIALS(BusinessCode.UserError.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    EMAIL_NOT_VERIFIED(BusinessCode.UserError.EMAIL_NOT_VERIFIED, HttpStatus.FORBIDDEN, "Email not verified"),

    // Role
    ROLE_NOT_FOUND(BusinessCode.RoleError.NOT_FOUND, HttpStatus.NOT_FOUND, "Role not found"),
    ROLE_ALREADY_EXISTS(BusinessCode.RoleError.ALREADY_EXISTS, HttpStatus.CONFLICT, "Role already exists"),

    // Permission
    PERMISSION_NOT_FOUND(BusinessCode.PermissionError.NOT_FOUND, HttpStatus.NOT_FOUND, "Permission not found"),
    PERMISSION_ALREADY_EXISTS(BusinessCode.PermissionError.ALREADY_EXISTS, HttpStatus.CONFLICT, "Permission already exists"),

    // Validation
    INVALID_INPUT(BusinessCode.Validation.INVALID_INPUT, HttpStatus.BAD_REQUEST, "Invalid input"),
    MISSING_FIELD(BusinessCode.Validation.MISSING_FIELD, HttpStatus.BAD_REQUEST, "Missing required field"),
    INVALID_FORMAT(BusinessCode.Validation.INVALID_FORMAT, HttpStatus.BAD_REQUEST, "Invalid format"),
    INVALID_EMAIL(BusinessCode.Validation.INVALID_EMAIL, HttpStatus.BAD_REQUEST, "Invalid email"),
    INVALID_PASSWORD(BusinessCode.Validation.INVALID_PASSWORD, HttpStatus.BAD_REQUEST, "Invalid password"),

    // System
    INTERNAL_SERVER_ERROR(BusinessCode.SystemError.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    SERVICE_UNAVAILABLE(BusinessCode.SystemError.SERVICE_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable"),
    DATABASE_ERROR(BusinessCode.SystemError.DATABASE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, "Database error")

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