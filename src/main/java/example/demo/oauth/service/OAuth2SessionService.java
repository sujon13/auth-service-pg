package example.demo.oauth.service;

import example.demo.util.RandomUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2SessionService {
    private static final String OAUTH_STATE = "oauth_state";
    private final RandomUtil randomUtil;

    private String generateStateParameter() {
        return randomUtil.getUUID();
    }

    public String storeStateInSession(HttpSession session) {
        final String state = generateStateParameter();
        session.setAttribute(OAUTH_STATE, state); // Store in session
        return state;
    }

    public void removeOAuthStateFromSession(HttpSession session) {
        session.removeAttribute(OAUTH_STATE);
    }

    public void checkStateParam(HttpSession session, final String state) {
        String storedState = (String) session.getAttribute(OAUTH_STATE);
        //removeOAuthStateFromSession(session); // Remove after use for security

        if (storedState == null || !storedState.equals(state)) {
            throw new AccessDeniedException("Invalid state parameter");
        }
    }
}
