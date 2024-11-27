package example.demo.oauth.model;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OAuthUserResponse {
    private Integer userId;
    private String accountId; // "117282101864315147161"
    private String message;
}
