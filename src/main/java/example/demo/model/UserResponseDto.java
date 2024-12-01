package example.demo.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponseDto {
    private Integer userId;
    private Integer id;
    private String title;
    private Boolean completed;
}
