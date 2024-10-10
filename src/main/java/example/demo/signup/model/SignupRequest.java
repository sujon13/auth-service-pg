package example.demo.signup.model;

import example.demo.signup.annotation.PasswordMatcher;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
@PasswordMatcher
public class SignupRequest {
    @NotBlank
    private String userName;
    @NotBlank
    @Email
    private String email;
    private String name;
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,10}$",
            message = "Password length must be between 6 and 10 and include at least one uppercase letter, one lowercase letter, and one digit without any special characters"
    )
    private String rawPassword;
    @NotBlank
    private String reTypeRawPassword;
}
