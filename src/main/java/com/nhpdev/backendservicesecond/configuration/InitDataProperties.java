package com.nhpdev.backendservicesecond.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "init-data")
public class InitDataProperties {
    private List<RoleProperties> roles;
    private UserProperties admin;
    private UserProperties superAdmin;

    @Getter
    @Setter
    public static class RoleProperties {
        private String name;
        private String description;
        private List<String> permissions;
    }

    @Getter
    @Setter
    public static class UserProperties {
        private String email;
        private String displayName;
        private String password;
    }
}
