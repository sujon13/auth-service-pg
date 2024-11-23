package example.demo.model;

import example.demo.enums.RoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class UserRoleRequest {
    @NotNull
    private Integer userId;
    @NotNull
    private RoleEnum role;
}
