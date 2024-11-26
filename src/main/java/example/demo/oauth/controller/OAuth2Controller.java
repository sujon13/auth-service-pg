package example.demo.oauth.controller;

import example.demo.oauth.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OAuth2Controller {
    private final OAuth2Service oAuth2Service;

    @GetMapping("/oauth2/callback")
    public void handleOAuthCallback(@RequestParam final String code) {
        var tokenResponse = oAuth2Service.getAccessToken(code);
        var userInfo = oAuth2Service.getUserInfo(tokenResponse.getAccessToken());
        //return "redirect:/home";
    }

    @GetMapping("/authenticate/google")
    public ResponseEntity<Void> generateAuthUrl() {
        URI uri = oAuth2Service.buildAuthorizationUrl();
        log.info("Google auth url: {}", uri.toString());
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }
}
