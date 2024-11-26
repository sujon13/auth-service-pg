package example.demo.oauth.service;


import example.demo.oauth.model.ExternalUserInfo;
import example.demo.oauth.model.ExternalUserResponse;
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
import java.util.Optional;

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

    public ExternalUserInfo getUserInfo(final String accessToken) {
        final String bearerToken = "Bearer " + accessToken;

        var responseEntity = restClient
                .get()
                .uri(userInfoUrl)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .toEntity(ExternalUserInfo.class);

        log.info("External User Info status code: {}", responseEntity.getStatusCode());
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Response Body: {}", responseEntity.getBody());
            return responseEntity.getBody();
        } else {
            return null;
        }
    }

    private User registerUser(ExternalUserInfo externalUserInfo) {
        User newUser = userService.createAndSaveUser(externalUserInfo);
        userRoleService.assignUserRole(newUser.getId());
        return newUser;
    }

    private User updateUser(User existingUser, ExternalUserInfo externalUserInfo) {
        log.info("User already exist with accountId: {}", existingUser.getAccountId());
        userService.updateUser(existingUser, externalUserInfo);
        return existingUser;
    }

    private User registerOrUpdateUser(ExternalUserInfo externalUserInfo) {
        return userService.findByAccountId(externalUserInfo.getAccountId())
                .map(existingUser -> updateUser(existingUser, externalUserInfo))
                .orElseGet(() -> registerUser(externalUserInfo));
    }

    @Transactional
    public User authenticateUserWithGoogle(final String authCode) {
        TokenResponse tokenResponse = getAccessToken(authCode);
        ExternalUserInfo externalUserInfo = getUserInfo(tokenResponse.getAccessToken());
        externalUserInfo.setAccountType(AccountType.GOOGLE);

        return registerOrUpdateUser(externalUserInfo);
    }

    private ExternalUserResponse createExternalUserResponse(final User user) {
        return ExternalUserResponse.builder()
                .userId(user.getId())
                .accountId(user.getAccountId())
                .build();
    }

    public ExternalUserResponse buildErrorResponse(final User user) {
        ExternalUserResponse externalUserResponse = createExternalUserResponse(user);
        final String errorMessage = "User is verified by Google but also needs to create a userName " +
                "to complete the registration";
        log.error(errorMessage);
        externalUserResponse.setMessage(errorMessage);
        return externalUserResponse;
    }

    public ExternalUserResponse buildJwtResponse(final User user) {
        ExternalUserResponse externalUserResponse = createExternalUserResponse(user);
        final String jwt = authenticationService.createAuthenticationToken(user);
        externalUserResponse.setMessage(jwt);
        return externalUserResponse;
    }
}