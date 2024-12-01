package example.demo.controller;

import example.demo.model.AuthenticationRequest;
import example.demo.model.UserResponse;
import example.demo.service.UserService;
import example.demo.service.auth.AuthenticationService;
import example.demo.service.auth.CookieService;
import example.demo.service.auth.PasswordService;
import example.demo.signup.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthenticationController {
    private final PasswordService passwordService;
    private final AuthenticationService authenticationService;
    private final CookieService cookieService;
    private final UserService userService;


    @PostMapping("/authenticate")
    public ResponseEntity<UserResponse> getAuthToken(@Valid @RequestBody AuthenticationRequest request) throws Exception {
        final String jwtToken = authenticationService.createAuthenticationToken(request);
        final ResponseCookie authCookie = cookieService.buildAuthCookie(jwtToken);
        final User user = userService.getUserByUserName(request.getUserName()).orElseThrow();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                .body(userService.buildUserResponse(user));
    }

    @GetMapping("/password")
    public String getPassword(@RequestParam String password) {
        return passwordService.encode(password);
    }

}
