package example.demo.oauth.service;

import example.demo.oauth.model.OAuthUser;
import example.demo.oauth.model.TokenRequest;
import example.demo.oauth.model.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleApiClient {
    @Value("${oauth2.google.access-token-url}")
    private String accessTokenUrl;

    @Value("${oauth2.google.client-id}")
    private String clientId;

    @Value("${oauth2.google.client-secret}")
    private String clientSecret;

    @Value("${oauth2.google.redirect-uri}")
    private String redirectUri;

    @Value("${oauth2.google.user-info-url}")
    private String userInfoUrl;

    private final RestClient restClient;


    private TokenRequest buildTokenRequest(final String authorizationCode) {
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

}