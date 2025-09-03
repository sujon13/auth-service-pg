package example.demo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserResponse {
    private int userId;
    private String userName;
    private String name;
    private String email;

    private List<RoleResponse> roles;
    private List<UserOfficeResponse> userOffices;
}
