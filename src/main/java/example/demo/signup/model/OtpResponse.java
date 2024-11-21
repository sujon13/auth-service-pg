package example.demo.signup.model;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
public class OtpResponse {
    private int id;
    private int userId;
}
