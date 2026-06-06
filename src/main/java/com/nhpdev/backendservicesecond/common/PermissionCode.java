package com.nhpdev.backendservicesecond.common;

public final class PermissionCode {
    private PermissionCode() {}
    // User
    public static final String USER_READ = "user:read";
    public static final String USER_CREATE = "user:create";
    public static final String USER_UPDATE = "user:update";
    public static final String USER_ASSIGN_ROLE = "user:assign_role";
    public static final String USER_DELETE_HARD = "user:delete_hard";
    public static final String USER_DELETE_SOFT = "user:delete_soft";

    // Role
    public static final String ROLE_READ = "role:read";
    public static final String ROLE_UPDATE = "role:update";


    // Permission
    public static final String PERMISSION_READ = "permission:read";
    public static final String PERMISSION_UPDATE = "permission:update";
    public static final String PERMISSION_ASSIGN = "permission:assign";

    //Post
    public static final String POST_DELETE_HARD = "post:delete_hard";
    public static final String POST_DELETE_SOFT_ANY = "post:delete_soft_any";
    public static final String POST_DELETE_SOFT_OWN = "post:delete_soft_own";
    public static final String POST_CREATE = "post:create";
    public static final String POST_UPDATE_OWN = "post:update_own";
    public static final String POST_READ = "post:read";

}
