package example.demo.service.auth;

import example.demo.signup.model.User;
import example.demo.util.Constants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class CookieService {
    private final AuthenticationService authenticationService;

    public ResponseCookie buildAuthCookie(final String jwtToken) {
        return ResponseCookie.from(Constants.ACCESS_TOKEN, jwtToken)
                .httpOnly(true)   // Make the cookie inaccessible from JS
                .secure(false)     // Ensure the cookie is only sent over HTTPS
                .path("/")        // Set the cookie's path (domain)
                .maxAge(Duration.ofMinutes(30)) // Cookie expiration
                .build();
    }

    public ResponseCookie buildAuthCookie(final User user) {
        final String jwtToken = authenticationService.createAuthenticationToken(user);
        return buildAuthCookie(jwtToken);
    }

    public Cookie clearAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(Constants.ACCESS_TOKEN, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return cookie;
    }
}
