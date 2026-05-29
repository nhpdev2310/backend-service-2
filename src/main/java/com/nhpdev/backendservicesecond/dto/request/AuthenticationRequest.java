package com.nhpdev.backendservicesecond.dto.request;

/***
 *
 * @param email
 * @param password
 */
public record AuthenticationRequest(
        String email,
        String password
) {
}
