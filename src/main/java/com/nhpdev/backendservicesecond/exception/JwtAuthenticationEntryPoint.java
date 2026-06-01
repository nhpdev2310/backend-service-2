package com.nhpdev.backendservicesecond.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import static com.nhpdev.backendservicesecond.exception.utils.JwtExceptionUtil.writeErrorResponse;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException, ServletException {
        writeErrorResponse(
                request,
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                ErrorCode.UNAUTHENTICATED
        );
    }
}
