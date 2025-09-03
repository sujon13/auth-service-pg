package example.demo.repository;

import example.demo.model.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface DesignationRepository extends JpaRepository<Designation, Integer> {
    List<Designation> findByIdIn(Collection<Integer> designationIds);
}