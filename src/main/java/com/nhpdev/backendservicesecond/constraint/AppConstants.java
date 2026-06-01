package com.nhpdev.backendservicesecond.constraint;

public final class AppConstants {
    private AppConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 5;

    public static final String URL_PREFIX = "/api/v1";

    public static final String DEFAULT_SORT = "createdAt";
}
