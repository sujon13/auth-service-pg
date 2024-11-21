package example.demo.signup.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class OtpSendRequest {
    private int userId;
    private String email;
}
