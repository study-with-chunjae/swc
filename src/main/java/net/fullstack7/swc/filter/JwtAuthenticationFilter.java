package net.fullstack7.swc.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.config.JwtTokenProvider;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import org.springframework.lang.NonNull;

@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String token = extractTokenFromCookie(request);
        String requestURI = request.getRequestURI();
        
        log.info("Requested URI: {}", requestURI);
        log.info("Token found: {}", token != null ? "yes" : "no");

        if (token != null && !jwtTokenProvider.validateToken(token)) {
            log.info("Invalid token, redirecting to login");
            response.sendRedirect("/");
            return;
        }

        log.info("Proceeding with request");
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> "accessToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/sign") || 
        path.startsWith("/assets") || 
        path.startsWith("/error") ||
        path.startsWith("/oauth2") ||
        path.equals("/") ||
        path.startsWith("/css") ||
        path.startsWith("/js") ||
        path.startsWith("/images");
    }
}
