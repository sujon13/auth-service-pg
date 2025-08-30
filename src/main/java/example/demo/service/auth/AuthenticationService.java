package example.demo.service.auth;

import example.demo.model.AuthenticationRequest;
import example.demo.service.UserService;
import example.demo.signup.model.User;
import example.demo.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    private void authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserNameOrEmail(), request.getPassword())
        );
    }

    public String createAuthenticationToken(final User user) {
        List<String> roleList = userService.getRolesOfUser(user.getId());
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.AUTHORITIES, roleList);
        claims.put(Constants.NAME, user.getName());
        return jwtUtil.generateToken(user.getUsername(), roleList, claims);
    }

    public String createAuthenticationToken(AuthenticationRequest request) {
        authenticate(request);

        final User user = userService.findByUserNameOrEmail(request.getUserNameOrEmail()).orElseThrow();
        return createAuthenticationToken(user);
    }
}
