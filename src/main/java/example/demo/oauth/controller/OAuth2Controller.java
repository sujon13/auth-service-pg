package example.demo.oauth.controller;

import example.demo.oauth.model.CallbackRequest;
import example.demo.oauth.model.UserNameRequest;
import example.demo.oauth.service.GoogleOAuthService;
import example.demo.oauth.service.OAuth2Service;
import example.demo.oauth.service.OAuth2SessionService;
import example.demo.service.UserService;
import example.demo.service.auth.CookieService;
import example.demo.signup.model.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth2")
public class OAuth2Controller {
    @Value("${oauth2.google.front-end-base-url}")
    private String frontEndBaseUrl;

    private final OAuth2Service oAuth2Service;
    private final GoogleOAuthService googleOAuthService;
    private final OAuth2SessionService oAuth2SessionService;
    private final UserService userService;
    private final CookieService cookieService;


    @GetMapping("/google/callback")
    public ResponseEntity<?> handleOAuthCallback(
            CallbackRequest callbackRequest, HttpSession session) {

        if (callbackRequest.getError() != null) {
            log.error(callbackRequest.getError());
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("User did not grant permission to access his/her information");
        }

        oAuth2SessionService.checkStateParam(session, callbackRequest.getState());
        oAuth2SessionService.removeOAuthStateFromSession(session);

        User user = googleOAuthService.authenticateUserWithGoogle(callbackRequest.getCode());

        if (user.getUsername() == null) {
            final String state = oAuth2SessionService.storeStateInSession(session);
            final URI uri = oAuth2Service.buildCreateUserNamePath(user, state);
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(uri)
                    .build();
        } else {
            final URI uri = URI.create(frontEndBaseUrl + "/");
            final ResponseCookie authCookie = cookieService.buildAuthCookie(user);
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                    .location(uri)
                    .build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUserName(@Valid @RequestBody UserNameRequest request, HttpSession session) {
        oAuth2SessionService.checkStateParam(session, request.getState());

        User user = userService.updateUserNameOfOAuthAccount(request);

        oAuth2SessionService.removeOAuthStateFromSession(session);

        final ResponseCookie authCookie = cookieService.buildAuthCookie(user);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                .build();
    }

    @GetMapping("/google/authenticate")
    public ResponseEntity<String> generateAuthUrl(HttpSession session) {
        final String state = oAuth2SessionService.storeStateInSession(session);

        URI uri = googleOAuthService.buildAuthorizationUrl(state);
        log.info("Google auth url: {}", uri.toString());
        return ResponseEntity.ok(uri.toString());
        //return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }
}
