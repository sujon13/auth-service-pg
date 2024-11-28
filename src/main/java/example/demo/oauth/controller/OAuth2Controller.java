package example.demo.oauth.controller;

import example.demo.oauth.model.CallbackRequest;
import example.demo.oauth.model.UserNameRequest;
import example.demo.oauth.service.GoogleOAuthService;
import example.demo.oauth.service.OAuth2Service;
import example.demo.oauth.service.OAuth2SessionService;
import example.demo.service.UserService;
import example.demo.service.auth.AuthenticationService;
import example.demo.signup.model.User;
import example.demo.util.RandomUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth2")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;
    private final GoogleOAuthService googleOAuthService;
    private final OAuth2SessionService oAuth2SessionService;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final RandomUtil randomUtil;


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

        var user = googleOAuthService.authenticateUserWithGoogle(callbackRequest.getCode());
        if (user.getUsername() == null) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(oAuth2Service.buildErrorResponse(user));
        } else {
            return ResponseEntity.ok(oAuth2Service.buildJwtResponse(user));
        }
    }

    @PostMapping("/register")
    public String registerUserName(@Valid @RequestBody UserNameRequest request) {
        User user = userService.updateUserNameOfOAuthAccount(request);
        return authenticationService.createAuthenticationToken(user);
    }

    private String generateStateParameter() {
        return randomUtil.getUUID();
    }

    @GetMapping("/google/authenticate")
    public ResponseEntity<Void> generateAuthUrl(HttpSession session) {
        final String state = generateStateParameter();
        oAuth2SessionService.storeStateInSession(session, state);

        URI uri = googleOAuthService.buildAuthorizationUrl(state);
        log.info("Google auth url: {}", uri.toString());
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }
}
