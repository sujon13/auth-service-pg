package example.demo.signup.repository;

import example.demo.signup.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OtpRepository extends JpaRepository<Otp, Integer> {
    @Query("""
        SELECT o FROM Otp o
        WHERE o.userId = :userId
        AND o.expireAt > CURRENT_TIMESTAMP
        AND o.used = false
    """)
    Optional<Otp> findByUserIdAndNotExpiredAndNotUsed(int userId);
}