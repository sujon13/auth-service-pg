package example.demo.model;

import jakarta.persistence.criteria.CriteriaBuilder;
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
