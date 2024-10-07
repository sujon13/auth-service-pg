package example.demo.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@Slf4j
public class SecurityService {
    private String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    //@Secured({"ROLE_ADMIN"})
    @PreAuthorize("hasAnyRole('ADMIN')")
    public double getRandom() {
        log.info("username: " + getUserName());
        return Math.random() * 10;
    }
}
