package example.demo.oauth.service;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OAuth2SessionService {
    private static final String OAUTH_STATE = "oauth_state";


    public void storeStateInSession(HttpSession session, final String state) {
        session.setAttribute(OAUTH_STATE, state); // Store in session
    }

    public void removeOAuthStateFromSession(HttpSession session) {
        session.removeAttribute(OAUTH_STATE);
    }

    public void checkStateParam(HttpSession session, final String state) {
        String storedState = (String) session.getAttribute(OAUTH_STATE);
        removeOAuthStateFromSession(session); // Remove after use for security

        if (storedState == null || !storedState.equals(state)) {
            throw new AccessDeniedException("Invalid state parameter");
        }
    }
}
