package example.demo.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRequest {
    @NotNull
    String userName;
    String name;

    @Override
    public String toString() {
        return "UserRequest{" +
                "userId=" + userName +
                ", name='" + name + '\'' +
                '}';
    }
}
