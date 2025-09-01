package example.demo.config;

import example.demo.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;


@Component
public class ApiKeyChecker {
    private final String SECRET_API_KEY = "secret-api-key";

    public boolean check(HttpServletRequest request) {
        String apiKey = request.getHeader(Constants.INTERNAL_API_KEY_HEADER);
        return SECRET_API_KEY.equals(apiKey);
    }
}
