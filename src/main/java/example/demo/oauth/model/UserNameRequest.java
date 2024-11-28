package example.demo.oauth.model;

import example.demo.util.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
public class UserNameRequest {
    @NotNull
    private Integer userId;

    @NotBlank
    private String accountId;

    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = Constants.USER_NAME_REGEXP, message = Constants.USER_NAME_ERROR_MESSAGE)
    private String userName;
}
