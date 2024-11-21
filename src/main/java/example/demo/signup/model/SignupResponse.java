package example.demo.signup.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignupResponse {
    private int userId;
    private String userName;
    private String email;
}
