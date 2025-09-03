package example.demo.repository;

import example.demo.model.UserOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserOfficeRepository extends JpaRepository<UserOffice, Integer> {
    List<UserOffice> findByUserId(int userId);

    List<UserOffice> findByUserIdIn(List<Integer> userIds);
}