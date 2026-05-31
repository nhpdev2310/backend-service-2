package com.nhpdev.backendservicesecond.exception;

public final class BusinessCode {
    private BusinessCode() {}
    public static final class AuthError {
        private AuthError() {}
        public static final String UNAUTHENTICATED = "AUTH_001";
        public static final String FORBIDDEN = "AUTH_002";
        public static final String TOKEN_EXPIRED = "AUTH_003";
        public static final String TOKEN_INVALID = "AUTH_004";
        public static final String REFRESH_TOKEN_EXPIRED = "AUTH_005";
    }

    public static final class UserError {
        private UserError() {}
        public static final String NOT_FOUND = "USER_001";
        public static final String ALREADY_EXISTS = "USER_002";
        public static final String DISABLED = "USER_003";
        public static final String INVALID_CREDENTIALS = "USER_004";
        public static final String EMAIL_NOT_VERIFIED = "USER_005";
    }

    public static final class RoleError {
        private RoleError() {}
        public static final String NOT_FOUND = "ROLE_001";
        public static final String ALREADY_EXISTS = "ROLE_002";
    }

    public static final class PermissionError {
        private PermissionError() {}
        public static final String NOT_FOUND = "PERM_001";
        public static final String ALREADY_EXISTS = "PERM_002";
    }

    public static final class Validation {
        private Validation() {}
        public static final String INVALID_INPUT = "VAL_001";
        public static final String MISSING_FIELD = "VAL_002";
        public static final String INVALID_FORMAT = "VAL_003";
        public static final String INVALID_EMAIL = "VAL_004";
        public static final String INVALID_PASSWORD = "VAL_005";
    }

    public static final class SystemError {
        private SystemError() {}
        public static final String INTERNAL_SERVER_ERROR = "SYS_001";
        public static final String SERVICE_UNAVAILABLE = "SYS_002";
        public static final String DATABASE_ERROR = "SYS_003";
    }
}
