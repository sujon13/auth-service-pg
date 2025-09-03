package example.demo.repository;

import example.demo.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    List<UserRole> getAllByUserId(Integer userId);

    List<UserRole> getAllByUserIdIn(Collection<Integer> userIds);
}