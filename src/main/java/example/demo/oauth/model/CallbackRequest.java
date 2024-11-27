package example.demo.oauth.model;

import lombok.*;

@Getter
@Setter
@Builder
public class CallbackRequest {
    private String code;
    private String error;
    private String state;
}
