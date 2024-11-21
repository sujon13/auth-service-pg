package example.demo.signup.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    @NotNull
    private String recipient;
    @NotNull
    private String subject;
    @NotNull
    private String body;
}