package example.demo.repository;

import example.demo.signup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserName(String userName);

    void deleteById(int userId);

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
    Optional<User> findByAccountIdOrEmail(String accountId, String email);

    @Query("SELECT u FROM User u WHERE u.isEmailVerified = true and u.isVerified = true")
    List<User> findAllVerifiedUsers();
}