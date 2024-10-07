package example.demo.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Extract username and password from the authentication request
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        String hashedPassword = passwordEncoder().encode("admin");

        // Custom logic to validate username and password
        if ("admin".equals(username) && passwordEncoder().matches(password, hashedPassword)) {
            // If authentication is successful, create a UserDetails object

            //UserDetails user1 = userDetailsService.loadUserByUsername(username);
            UserDetails user = User.withUsername(username)
                    .password(hashedPassword)
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .build();

            // Return an authenticated token
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } else {
            // If authentication fails, throw an exception
            throw new AuthenticationException("Invalid credentials") {};
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // This provider supports UsernamePasswordAuthenticationToken authentication type
        //return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
        return true;
    }
}
