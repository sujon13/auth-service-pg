package example.demo.service.auth;

import example.demo.signup.model.User;
import example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationManagerImp implements AuthenticationManager {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String rawPassword = authentication.getCredentials().toString();
        User user = userService.getUserByUserName(authentication.getName()).orElseThrow();
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Password is Wrong!");
        }
        return authentication;
    }
}
