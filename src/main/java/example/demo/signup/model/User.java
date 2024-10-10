package example.demo.signup.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


@Setter
@Getter
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
//@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "user_name", unique = true)
    private String userName;

    @NotNull
    @Column(name = "email", unique = true)
    private String email;

    @NotNull
    @Column(name = "password")
    @JsonIgnore
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "validated")
    private boolean validated = false;

    //@CreatedBy
    @Column(name = "created_by", updatable = false)
    @JsonIgnore
    private String createdBy;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    @JsonProperty("userName")
    public String getUsername() {
        return this.userName;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }
}