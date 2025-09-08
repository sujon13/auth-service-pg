package example.demo.oauth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import example.demo.signup.enums.AccountType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OAuthUser {
    @JsonProperty("sub")
    private String accountId; // "117282101864315147161"

    private String email;
    private String name;

    private AccountType accountType = AccountType.GOOGLE;
}