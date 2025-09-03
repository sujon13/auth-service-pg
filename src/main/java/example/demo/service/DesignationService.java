package example.demo.service;

import example.demo.exception.NotFoundException;
import example.demo.model.Designation;
import example.demo.repository.DesignationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DesignationService {
    private final DesignationRepository designationRepository;

    public Designation findById(final int id) {
        return designationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Designation not found with id " + id ));
    }

    public List<Designation> findByIds(Collection<Integer> designationIds) {
        Set<Integer> ids = designationIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
        return designationRepository.findByIdIn(ids);
    }

    public List<Designation> findAll() {
        return designationRepository.findAll();
    }
}

