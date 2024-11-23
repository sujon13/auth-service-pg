package example.demo.service;

import example.demo.enums.RoleEnum;
import example.demo.model.UserRole;
import example.demo.model.UserRoleRequest;
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

    public List<UserRole> retrieveUserRoles(final int userId) {
        return userRoleRepository.getAllByUserId(userId);
    }

    public List<Integer> retrieveRoleIds(final int userId) {
        return retrieveUserRoles(userId)
                .stream()
                .map(UserRole::getRoleId)
                .toList();
    }

    private UserRole createUserRole(final int userId, final RoleEnum role) {
        final UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(role.getId());
        return userRole;
    }

    @Transactional
    public UserRole assign(final int userId, final RoleEnum role) {
        final UserRole userRole = createUserRole(userId, role);
        return userRoleRepository.save(userRole);
    }

    @Transactional
    public UserRole assign(final UserRoleRequest request) {
        return assign(request.getUserId(), request.getRole());
    }

}
