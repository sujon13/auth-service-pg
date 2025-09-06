package example.demo.service.auth;

import example.demo.config.SecurityConfig;
import example.demo.service.UserService;
import example.demo.util.Constants;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    private String extractJwtToken(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else return null;
    }

    private String extractJwtTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Constants.ACCESS_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean isPublicEndpoint(final HttpServletRequest request) {
        final String requestPath = request.getRequestURI();
        return SecurityConfig.getPublicEndpoints()
                .stream()
                .anyMatch(pattern -> requestPath.matches(pattern.replace("**", ".*")));
    }

    private boolean isOptionsRequest(final HttpServletRequest request) {
        return HttpMethod.OPTIONS.matches(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (isPublicEndpoint(request) || isOptionsRequest(request)) {
            // Skip token validation for public endpoints
            chain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();

        // Skip JWT for API key-protected endpoint
        if (path.equals("/api/v1/users")) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = extractJwtToken(request);
        if (jwt == null) {
            log.info("Token found from the cookie");
            jwt = extractJwtTokenFromCookie(request);
        }

        String username = null;

        if (jwt != null) {
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (MalformedJwtException e) {
                handleJwtException(response, "Invalid JWT Token", e.getMessage());
            } catch (SignatureException e) {
                handleJwtException(response, "Invalid JWT Signature", e.getMessage());
            } catch (SecurityException e) {
                handleJwtException(response, "Invalid JWT Security", e.getMessage());
            } catch (ExpiredJwtException e) {
                handleJwtException(response, "JWT Token has expired", e.getMessage());
            }
            if (username == null) {
                log.error("Username not found in JWT Token");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // skip recheck user if it is altered meanwhile
            List<? extends GrantedAuthority> authorities = jwtUtil.extractRoles(jwt);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    username, null, authorities);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        chain.doFilter(request, response);
    }

    private void handleJwtException(HttpServletResponse response, String message, String errorMessage) throws IOException {
        log.error(message);
        log.error(errorMessage);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(message);
    }
}