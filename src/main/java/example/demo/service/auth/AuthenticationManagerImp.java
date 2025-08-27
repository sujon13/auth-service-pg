package example.demo.service.auth;

import example.demo.exception.EmailNotVerifiedException;
import example.demo.exception.NotVerifiedException;
import example.demo.service.UserService;
import example.demo.service.Util;
import example.demo.signup.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationManagerImp implements AuthenticationManager {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String rawPassword = authentication.getCredentials().toString();
        Optional<User> optionalUser = userService.findByUserNameOrEmail(authentication.getName());
        if (optionalUser.isEmpty()) {
            boolean isEmail = Util.isEmail(authentication.getName());
            if (isEmail) {
                log.error("Email {} not found", authentication.getName());
                throw new BadCredentialsException("Email or Password is Wrong!");
            } else {
                log.error("UserName {} not found", authentication.getName());
                throw new BadCredentialsException("UserName or Password is Wrong!");
            }
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            log.error("password does not match for user {}", authentication.getName());
            throw new BadCredentialsException("UserName/Email or Password is Wrong!");
        }

        if (!user.isEmailVerified()) {
            log.error("Email is not verified for user {}", authentication.getName());
            throw new EmailNotVerifiedException("Email is not verified");
        }

        if (!user.isVerified()) {
            log.error("User {} is not verified", authentication.getName());
            throw new NotVerifiedException("User is not verified");
        }

        return authentication;
    }
}
