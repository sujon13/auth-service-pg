package example.demo.service;

import example.demo.model.Role;
import example.demo.signup.model.User;
import example.demo.model.UserRequest;
import example.demo.repository.UserRepository;
import example.demo.service.auth.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final PasswordService passwordService;

    //@PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Transactional
    @PreAuthorize("#userName == authentication.name")
    public Optional<User> updateUser(String userName, UserRequest userRequest) {
        Optional<User> optionalUser = userRepository.findByUserName(userName);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (optionalUser.isPresent()) {
            optionalUser.get().setName(userRequest.getName());
            return optionalUser;
        } else {
            log.error("User not found with userName: " + userName);
            return Optional.empty();
        }
    }

    private User createUserFromRequest(UserRequest userRequest) {
        User user = new User();
        user.setUserName(userRequest.getUserName());
        user.setName(userRequest.getName());
        user.setPassword(passwordService.encode(userRequest.getRawPassword()));
        return user;
    }

    @Transactional
    public Optional<User> saveUser(UserRequest userRequest) {
        User user = createUserFromRequest(userRequest);
        try {
            userRepository.save(user);
            return Optional.of(user);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return Optional.empty();
        }
    }

    @Transactional
    public void deleteById(int id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            log.error(emptyResultDataAccessException.getMessage());
            throw new RuntimeException("not found");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<String> getRolesOfUser(User user) {
        List<Integer> roleIds = userRoleService.retrieveRoleIds(user.getId());
        return roleService.retrieveRoles(roleIds)
                .stream()
                .map(Role::getName)
                .map(role -> "ROLE_" + role)
                .toList();
    }
}
