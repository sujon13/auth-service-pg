package example.demo.oauth.service;


import example.demo.oauth.model.OAuthUser;
import example.demo.oauth.model.OAuthUserResponse;
import example.demo.oauth.model.TokenRequest;
import example.demo.oauth.model.TokenResponse;
import example.demo.service.UserRoleService;
import example.demo.service.UserService;
import example.demo.service.auth.AuthenticationService;
import example.demo.signup.enums.AccountType;
import example.demo.signup.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2Service {
    private static final String RESPONSE_TYPE = "code";
    private static final String ACCESS_TYPE = "offline";
    private static final String SCOPE = "openid email profile";
    private static final String PROMPT = "consent select_account";

    @Value("${oauth2.google.auth-url}")
    private String authorizationUrl;

    @Value("${oauth2.google.access-token-url}")
    private String accessTokenUrl;

    @Value("${oauth2.google.client-id}")
    private String clientId;;

    @Value("${oauth2.google.client-secret}")
    private String clientSecret;

    @Value("${oauth2.google.redirect-uri}")
    private String redirectUri;

    @Value("${oauth2.google.user-info-url}")
    private String userInfoUrl;

    private final RestClient restClient;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final UserRoleService userRoleService;

    public URI buildAuthorizationUrl(final String state) {
        return UriComponentsBuilder.fromHttpUrl(authorizationUrl)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", RESPONSE_TYPE)
                .queryParam("scope", SCOPE)
                .queryParam("state", state)
                .queryParam("access_type", ACCESS_TYPE)
                .queryParam("prompt", PROMPT)
                .build()
                .toUri();
    }

    private TokenRequest buildTokenRequest(final String  authorizationCode) {
        return TokenRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(authorizationCode)
                .redirectUri(redirectUri)
                .build();
    }

    public TokenResponse getAccessToken(final String authorizationCode) {
        TokenRequest requestBody = buildTokenRequest(authorizationCode);
        log.info("Access Token request body: {}", requestBody);

        var responseEntity = restClient
                .post()
                .uri(accessTokenUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(TokenResponse.class);

        log.info("Access token status code: {}", responseEntity.getStatusCode());
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Response Body: {}", responseEntity.getBody());
            return responseEntity.getBody();
        } else {
            return null;
        }
    }

    public OAuthUser getUserInfo(final String accessToken) {
        final String bearerToken = "Bearer " + accessToken;

        var responseEntity = restClient
                .get()
                .uri(userInfoUrl)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .toEntity(OAuthUser.class);

        log.info("External User Info status code: {}", responseEntity.getStatusCode());
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Response Body: {}", responseEntity.getBody());
            return responseEntity.getBody();
        } else {
            return null;
        }
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

    private User registerOrUpdateUser(OAuthUser oAuthUser) {
        return userService.findByAccountIdOrEmail(oAuthUser.getAccountId(), oAuthUser.getEmail())
                .map(existingUser -> updateUser(existingUser, oAuthUser))
                .orElseGet(() -> registerUser(oAuthUser));
    }

    @Transactional
    public User authenticateUserWithGoogle(final String authCode) {
        TokenResponse tokenResponse = getAccessToken(authCode);
        OAuthUser oAuthUser = getUserInfo(tokenResponse.getAccessToken());
        oAuthUser.setAccountType(AccountType.GOOGLE);

        return registerOrUpdateUser(oAuthUser);
    }

    private OAuthUserResponse createOAuthUserResponse(final User user) {
        return OAuthUserResponse.builder()
                .userId(user.getId())
                .accountId(user.getAccountId())
                .build();
    }

    public OAuthUserResponse buildErrorResponse(final User user) {
        OAuthUserResponse oAuthUserResponse = createOAuthUserResponse(user);
        final String errorMessage = "User is verified by Google but also needs to create a userName " +
                "to complete the registration";
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
}