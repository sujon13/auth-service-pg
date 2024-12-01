package example.demo.service.auth;

import example.demo.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class CookieService {
    public ResponseCookie buildAuthCookie(final String jwtToken) {
        return ResponseCookie.from(Constants.ACCESS_TOKEN, jwtToken)
                .httpOnly(true)   // Make the cookie inaccessible from JS
                .secure(false)     // Ensure the cookie is only sent over HTTPS
                .path("/")        // Set the cookie's path (domain)
                .maxAge(Duration.ofMinutes(30)) // Cookie expiration
                .build();
    }
}
