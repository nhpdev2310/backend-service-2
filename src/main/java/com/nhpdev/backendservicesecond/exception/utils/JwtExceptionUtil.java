package com.nhpdev.backendservicesecond.exception.utils;

import com.nhpdev.backendservicesecond.exception.ErrorCode;
import com.nhpdev.backendservicesecond.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

public final class JwtExceptionUtil {
    private static final JsonMapper MAPPER = new JsonMapper();
    private JwtExceptionUtil() {}
    public static void writeErrorResponse(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            int httpStatus,
            ErrorCode errorCode) throws IOException {

        response.setStatus(httpStatus);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = ErrorResponse.of(errorCode, request.getRequestURI());

        response.getWriter().write(MAPPER.writeValueAsString(errorResponse));
        response.flushBuffer();
    }
}

