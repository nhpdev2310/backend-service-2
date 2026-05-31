package com.nhpdev.backendservicesecond.configuration;

import com.nhpdev.backendservicesecond.common.constraint.JwtConstants;
import com.nhpdev.backendservicesecond.dto.response.RoleDetailResponse;
import com.nhpdev.backendservicesecond.service.JwtService;
import com.nhpdev.backendservicesecond.service.RoleService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>>{

    private final JwtService jwtService;
    private final RoleService roleService;

    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt source) {
        //DONE: Extract authorities from jwt -> ["ADMIN, SUPOER_ADMIN"]
        List<String> roleNames = jwtService.extractAuthorities(source.getClaim(JwtConstants.AUTHORITIES));
        if (roleNames == null || roleNames.isEmpty()) {
            return Collections.emptyList();
        }
        //DONE: Get all permission based on authorities
        List<RoleDetailResponse> roleDetails = RoleDetailResponse.ofAll(roleService.getAllPermissionByRoleNames(roleNames));
        //DONE: Combine all of that at a grantedAuthorities in which ROLE go with the prefix "ROLE_"
        return roleDetails.stream().flatMap(rd -> {
            Stream<GrantedAuthority> roleStream = Stream.of(
                    new SimpleGrantedAuthority(JwtConstants.AUTHORIZE_PREFIX + rd.getName()));
            Stream<GrantedAuthority> permissionStream = rd.getPermissions() == null ? Stream.empty()
                    : rd.getPermissions().stream().map(SimpleGrantedAuthority::new);
            return Stream.concat(roleStream, permissionStream);
        }).collect(Collectors.toList());
    }
}
