package example.demo.service.auth;

import example.demo.model.AuthenticationRequest;
import example.demo.service.UserService;
import example.demo.signup.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    private void authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
        );
    }

    public String createAuthenticationToken(User user) {
        List<String> roleList = userService.getRolesOfUser(user.getId());
        return jwtUtil.generateToken(user.getUsername(), roleList);
    }

    public String createAuthenticationToken(AuthenticationRequest request) {
        authenticate(request);

        //final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final User user = userService.getUserByUserName(request.getUserName()).orElseThrow();
        return createAuthenticationToken(user);
    }
}
