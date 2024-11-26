package example.demo.oauth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserNameRequest {
    @NotNull
    private Integer userId;
    @NotBlank
    private String accountId;
    @NotBlank
    private String userName;
}
