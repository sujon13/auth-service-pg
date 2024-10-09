package example.demo.service;

import example.demo.model.UserRole;
import example.demo.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;

    public List<UserRole> retrieveUserRoles(Integer userId) {
        return userRoleRepository.getAllByUserId(userId);
    }

    public List<Integer> retrieveRoleIds(Integer userId) {
        return retrieveUserRoles(userId)
                .stream()
                .map(UserRole::getRoleId)
                .toList();
    }
}
