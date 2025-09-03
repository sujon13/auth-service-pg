package example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "user_offices", indexes = {
        @Index(name = "idx_user_offices_user_id", columnList = "user_id"),
})
@NoArgsConstructor
@AllArgsConstructor
public class UserOffice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "designation_id", nullable = false)
    private Integer designationId;

    @Column(name = "office_id", nullable = false)
    private Integer officeId;
}