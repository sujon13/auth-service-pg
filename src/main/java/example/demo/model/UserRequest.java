package example.demo.model;

import example.demo.enums.RoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRequest {
    private String userName;
    private String email;
    private String name;
    private String rawPassword;
    private Boolean isEmailVerified;
    private Boolean isVerified;
    private RoleEnum role;
}
