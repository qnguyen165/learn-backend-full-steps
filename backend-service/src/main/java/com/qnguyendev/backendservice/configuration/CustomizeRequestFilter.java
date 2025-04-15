package com.qnguyendev.backendservice.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qnguyendev.backendservice.common.TokenType;
import com.qnguyendev.backendservice.service.CustomUserDetailsService;
import com.qnguyendev.backendservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Slf4j(topic = "CUSTOMIZE-REQUEST-FILTER")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomizeRequestFilter extends OncePerRequestFilter {

    JwtService jwtService;
    CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("{} {}", request.getMethod(), request.getRequestURI());

        final String authHeader = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasLength(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("token: {}...", token.substring(0, 20));
            String username = "";
            try {
                username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
                log.info("username: {}", username);
            } catch (AccessDeniedException e) {
                log.info(e.getMessage());
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(errorResponse(request.getRequestURI(), e.getMessage()));
                return;
            }

            UserDetails user = customUserDetailsService.userDetailsService().loadUserByUsername(username);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);

            filterChain.doFilter(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Create error response with pretty template
     * @param message
     * @return
     */
    private String errorResponse(String url, String message) {
        try {
            ErrorResponse error = new ErrorResponse();
            error.setTimestamp(new Date());
            error.setStatus(HttpServletResponse.SC_FORBIDDEN);
            error.setPath(url);
            error.setError("Forbidden");
            error.setMessage(message);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(error);
        } catch (Exception e) {
            return ""; // Return an empty string if serialization fails
        }
    }

    @Setter
    @Getter
    private class ErrorResponse {
        private Date timestamp;
        private int status;
        private String path;
        private String error;
        private String message;
    }
}
