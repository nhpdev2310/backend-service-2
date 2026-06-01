package com.nhpdev.backendservicesecond.exception;

import static com.nhpdev.backendservicesecond.exception.utils.JwtExceptionUtil.writeErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(@NonNull HttpServletRequest request,
                       @NonNull HttpServletResponse response,
                       @NonNull AccessDeniedException accessDeniedException) throws IOException, ServletException {
        writeErrorResponse(
                request,
                response,
                HttpServletResponse.SC_FORBIDDEN,
                ErrorCode.FORBIDDEN
        );
    }

}
