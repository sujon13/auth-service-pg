package example.demo.repository;

import example.demo.model.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface OfficeRepository extends JpaRepository<Office, Integer> {
    List<Office> findByIdIn(Collection<Integer> officeIds);
}