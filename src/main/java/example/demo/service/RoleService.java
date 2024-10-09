package example.demo.service;

import example.demo.model.Role;
import example.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> retrieveRoles(List<Integer> ids) {
        return roleRepository.findAllById(ids);
    }
}
