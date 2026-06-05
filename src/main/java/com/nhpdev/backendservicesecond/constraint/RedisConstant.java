package com.nhpdev.backendservicesecond.constraint;

public final class RedisConstant {
    public static final String ROLE_PERMISSIONS_CACHE = "role_permission";
    public static final String USER_DETAIL_LIST_CACHE = "users";
    public static final String BLACKLIST_ACCESS_PREFIX = "blacklist:access_token:";
    public static final String WHITELIST_REFRESH_PREFIX = "whitelist:session_key:";
    public static final String VERIFY_EMAIL_PREFIX = "verify_email:";
    public static final long SCAN_OPTION_COUNT = 100;
    public static final String LOGOUT_ALL_PREFIX = "user:logout_all:";
    private RedisConstant() {}
}
