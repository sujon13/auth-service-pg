package example.demo.service;

import example.demo.exception.NotFoundException;
import example.demo.model.Office;
import example.demo.repository.OfficeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfficeService {
    private final OfficeRepository officeRepository;

    public Office findById(Integer id) {
        return officeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Office not found not with id " + id));
    }

    public List<Office> findAll() {
        return officeRepository.findAll();
    }

    public List<Office> findByIds(List<Integer> officeIds) {
        Set<Integer> officeIdsSet = officeIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());

        return officeRepository.findByIdIn(officeIdsSet);
    }
}
