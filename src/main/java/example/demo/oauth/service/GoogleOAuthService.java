package example.demo.oauth.service;

import example.demo.oauth.model.OAuthUser;
import example.demo.oauth.model.TokenResponse;
import example.demo.signup.enums.AccountType;
import example.demo.signup.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleOAuthService {
    private static final String RESPONSE_TYPE = "code";
    private static final String ACCESS_TYPE = "offline";
    private static final String SCOPE = "openid email profile";
    private static final String PROMPT = "consent select_account";

    @Value("${oauth2.google.auth-url}")
    private String authorizationUrl;

    @Value("${oauth2.google.client-id}")
    private String clientId;

    @Value("${oauth2.google.redirect-uri}")
    private String redirectUri;

    private final OAuth2Service oauth2Service;
    private final GoogleApiClient googleApiClient;


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

    public User authenticateUserWithGoogle(final String authCode) {
        TokenResponse tokenResponse = googleApiClient.getAccessToken(authCode);
        OAuthUser oAuthUser = googleApiClient.getUserInfo(tokenResponse.getAccessToken());
        oAuthUser.setAccountType(AccountType.GOOGLE);

        return oauth2Service.registerOrUpdateUser(oAuthUser);
    }

}
