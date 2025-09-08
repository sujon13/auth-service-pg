package example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user_role")
@NoArgsConstructor
//@EntityListeners(AuditingEntityListener.class)
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "user_id")
    private Integer userId;

    @NotNull
    @Column(name = "role_id")
    private Integer roleId;
}
