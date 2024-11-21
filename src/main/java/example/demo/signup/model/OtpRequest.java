package example.demo.signup.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OtpRequest {
    private int id;
    private int userId;
    private int otp;
}
