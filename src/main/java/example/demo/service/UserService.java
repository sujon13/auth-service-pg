package example.demo.service;

import example.demo.exception.EntryAlreadyExistsException;
import example.demo.exception.NotFoundException;
import example.demo.model.Role;
import example.demo.model.UserRequest;
import example.demo.model.UserResponse;
import example.demo.oauth.model.OAuthUser;
import example.demo.oauth.model.UserNameRequest;
import example.demo.repository.UserRepository;
import example.demo.service.auth.PasswordService;
import example.demo.signup.model.User;
import example.demo.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    private final UserUtil userUtil;

    //@PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findById(final int id) {
        return userRepository.findById(id);
    }

    public User getUser(final int id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));
    }

    public Optional<User> findByAccountIdOrEmail(final String accountId, final String email) {
        return userRepository.findByAccountIdOrEmail(accountId, email);
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

    private User createUser(OAuthUser oAuthUser) {
        User user = new User();
        user.setAccountType(oAuthUser.getAccountType());
        user.setAccountId(oAuthUser.getAccountId());
        user.setEmail(oAuthUser.getEmail());
        user.setName(oAuthUser.getName());
        return user;
    }

    @Transactional
    public User createAndSaveUser(OAuthUser oAuthUser) {
        User newUser = createUser(oAuthUser);
        userRepository.save(newUser);
        return newUser;
    }

    private void checkUserNameExistence(final String userName) {
        if (userRepository.existsByUserName(userName)) {
            throw new EntryAlreadyExistsException("User Name " + userName + " is already taken.");
        }
    }

    @Transactional
    public User updateUserNameOfOAuthAccount(final UserNameRequest request) {
        checkUserNameExistence(request.getUserName());

        final User user = getUser(request.getUserId());
        if (user.getAccountId().equals(request.getAccountId())) {
            user.setUserName(request.getUserName());
            makeUserVerified(user);
            return user;
        } else {
            final String errorMessage = "AccountId does not match";
            log.error(errorMessage);
            throw new AccessDeniedException(errorMessage);
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

    public List<String> getRolesOfUser(final int userId) {
        List<Integer> roleIds = userRoleService.retrieveRoleIds(userId);
        return roleService.retrieveRoles(roleIds)
                .stream()
                .map(Role::getName)
                .map(role -> "ROLE_" + role)
                .toList();
    }

    public void makeUserVerified(User user) {
        user.setVerified(true);
    }

    @Transactional
    public void makeUserVerified(final int userId) {
        User user = getUser(userId);
        makeUserVerified(user);
    }

    public UserResponse buildUserResponse(final User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .userName(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public UserResponse buildUserResponse() {
        final String userName = userUtil.getUserName();
        final User user = getUserByUserName(userName).orElseThrow();
        return buildUserResponse(user);
    }

}
