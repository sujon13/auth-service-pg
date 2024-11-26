package example.demo.oauth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserInfo {
    @JsonProperty("sub")
    private String googleId; // "117282101864315147161"

    private String email;
    private String name;
}