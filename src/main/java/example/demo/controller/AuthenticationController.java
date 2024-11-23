package example.demo.controller;

import example.demo.model.AuthenticationRequest;
import example.demo.service.auth.AuthenticationService;
import example.demo.service.auth.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthenticationController {
    private final PasswordService passwordService;
    private final AuthenticationService authenticationService;


    @PostMapping("/authenticate")
    public String createAuthenticationToken(@RequestBody AuthenticationRequest request) throws Exception {
        return authenticationService.createAuthenticationToken(request);
    }

    @GetMapping("/password")
    public String getPassword(@RequestParam String password) {
        return passwordService.encode(password);
    }

}
