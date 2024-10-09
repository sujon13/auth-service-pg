package example.demo.controller;

import example.demo.model.AuthenticationRequest;
import example.demo.model.User;
import example.demo.service.UserService;
import example.demo.service.auth.JwtUtil;
import example.demo.service.auth.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordService passwordService;

    @PostMapping("/authenticate")
    public String createAuthenticationToken(@RequestBody AuthenticationRequest request) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        //final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final User user = userService.getUserByUserName(request.getUsername()).orElseThrow();
        final String jwt = jwtUtil.generateToken(user.getUsername());
        //final String jwt = "abc";
        return jwt;
    }

    @GetMapping("/password")
    public String getPassword(@RequestParam String password) {
        return passwordService.encode(password);
    }

    @GetMapping("/test-authenticate")
    public String testAuthenticatedUser() {
        return "test response (only authenticated user will receive!";
    }
}
