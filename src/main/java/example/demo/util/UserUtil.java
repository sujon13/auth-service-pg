package example.demo.util;

import example.demo.enums.RoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserUtil {
    public String getUserName(Authentication authentication) {
        return authentication != null ? authentication.getName() : null;
    }

    public String getUserName() {
        return getUserName(SecurityContextHolder.getContext().getAuthentication());
    }

    public boolean hasAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities()
                .stream()
                //.anyMatch(auth -> auth.getAuthority().equals("ROLE" + role));
                .anyMatch(auth ->
                        java.util.Arrays.stream(roles)
                                .anyMatch(role -> auth.getAuthority().equals("ROLE_" + role))
                );
    }

    public List<RoleEnum> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> RoleEnum.valueOf(auth.replace("ROLE_", "")))
                .toList();
    }

    public boolean isAdmin() {
        return hasAnyRole("ADMIN");
    }

    public boolean isUser() {
        return hasAnyRole("USER");
    }

}
