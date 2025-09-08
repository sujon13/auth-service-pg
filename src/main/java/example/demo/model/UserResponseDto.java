package example.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserResponseDto {
    private Integer userId;
    private Integer id;
    private String title;
    private Boolean completed;
}
