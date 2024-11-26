package example.demo.oauth.service;


import example.demo.oauth.model.TokenRequest;
import example.demo.oauth.model.TokenResponse;
import example.demo.oauth.model.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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


    public URI buildAuthorizationUrl() {
        return UriComponentsBuilder.fromHttpUrl(authorizationUrl)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", RESPONSE_TYPE)
                .queryParam("scope", SCOPE)
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

    public UserInfo getUserInfo(final String accessToken) {
        final String bearerToken = "Bearer " + accessToken;

        var responseEntity = restClient
                .get()
                .uri(userInfoUrl)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .toEntity(UserInfo.class);

        log.info("User Info status code: {}", responseEntity.getStatusCode());
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Response Body: {}", responseEntity.getBody());
            return responseEntity.getBody();
        } else {
            return null;
        }
    }
}