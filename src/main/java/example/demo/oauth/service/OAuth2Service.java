package example.demo.oauth.service;


import example.demo.oauth.model.OAuthUser;
import example.demo.oauth.model.OAuthUserResponse;
import example.demo.service.UserRoleService;
import example.demo.service.UserService;
import example.demo.service.auth.AuthenticationService;
import example.demo.service.auth.CookieService;
import example.demo.signup.enums.AccountType;
import example.demo.signup.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuth2Service {
    @Value("${oauth2.google.front-end-base-url}")
    private String frontEndBaseUrl;

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final UserRoleService userRoleService;
    private final CookieService cookieService;

    public URI buildCreateUserNamePath(final User user, final String state) {
        return UriComponentsBuilder.fromHttpUrl(frontEndBaseUrl + "/createusername")
                .queryParam("userId", user.getId())
                .queryParam("accountId", user.getAccountId())
                .queryParam("state", state)
                .build()
                .toUri();
    }

    private User registerUser(OAuthUser oAuthUser) {
        User newUser = userService.createAndSaveUser(oAuthUser);
        userRoleService.assignUserRole(newUser.getId());
        return newUser;
    }

    private User updateUser(User existingUser, OAuthUser oAuthUser) {
        log.info("User already exist with accountId: {}, email: {}", existingUser.getAccountId(), existingUser.getEmail());
        if (existingUser.getAccountId() == null)
            existingUser.setAccountId(oAuthUser.getAccountId());

        if (existingUser.isRegularUser()) {
            if (existingUser.getName() == null)
                existingUser.setName(oAuthUser.getName());
        } else {
            if (oAuthUser.getName() != null)
                existingUser.setName(oAuthUser.getName());
        }
        return existingUser;
    }

    @Transactional
    public User registerOrUpdateUser(OAuthUser oAuthUser) {
        return userService.findByAccountIdOrEmail(oAuthUser.getAccountId(), oAuthUser.getEmail())
                .map(existingUser -> updateUser(existingUser, oAuthUser))
                .orElseGet(() -> registerUser(oAuthUser));
    }

    private OAuthUserResponse createOAuthUserResponse(final User user) {
        return OAuthUserResponse.builder()
                .userId(user.getId())
                .accountId(user.getAccountId())
                .build();
    }

    private String buildErrorMessage(AccountType accountType) {
        final String vendor = accountType.name().toLowerCase();
        return "User is verified by " + StringUtils.capitalize(vendor) +
                " but also needs to create a userName to complete the registration";
    }

    public OAuthUserResponse buildErrorResponse(final User user) {
        OAuthUserResponse oAuthUserResponse = createOAuthUserResponse(user);

        final String errorMessage = buildErrorMessage(user.getAccountType());
        log.error(errorMessage);

        oAuthUserResponse.setMessage(errorMessage);
        return oAuthUserResponse;
    }

    public OAuthUserResponse buildJwtResponse(final User user) {
        OAuthUserResponse oAuthUserResponse = createOAuthUserResponse(user);
        final String jwt = authenticationService.createAuthenticationToken(user);
        oAuthUserResponse.setMessage(jwt);
        return oAuthUserResponse;
    }

    public ResponseCookie getAuthCookie(final User user) {
        final String jwt = authenticationService.createAuthenticationToken(user);
        return cookieService.buildAuthCookie(jwt);
    }
}