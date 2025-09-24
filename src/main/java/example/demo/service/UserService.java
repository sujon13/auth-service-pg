package example.demo.service;

import example.demo.enums.RoleEnum;
import example.demo.exception.EntryAlreadyExistsException;
import example.demo.exception.NotFoundException;
import example.demo.model.*;
import example.demo.oauth.model.OAuthUser;
import example.demo.oauth.model.UserNameRequest;
import example.demo.repository.UserRepository;
import example.demo.service.auth.PasswordService;
import example.demo.signup.model.User;
import example.demo.util.Constants;
import example.demo.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final UserOfficeService userOfficeService;
    private final PasswordService passwordService;
    private final UserUtil userUtil;

    //@PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers(List<String> userNames) {
        List<User> users = userRepository.findAllByUserNameIn(userNames);
        return buildUserResponseList(users);
    }

    public List<User> findAllVerifiedUsers() {
        return userRepository.findAllVerifiedUsers();
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

    public Optional<User> findByUserNameOrEmail(final String userNameOrEmail) {
        boolean isEmail = Util.isEmail(userNameOrEmail);
        return isEmail
                ? userRepository.findByEmail(userNameOrEmail)
                : userRepository.findByUserName(userNameOrEmail);
    }

    private void updateUser(User user, UserRequest userRequest) {
        user.setName(userRequest.getName());
    }


    @Transactional
    //@PreAuthorize("#userName == authentication.name")
    public Optional<User> updateUser(final int userId, UserRequest userRequest) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            updateUser(optionalUser.get(), userRequest);
            return optionalUser;
        } else {
            log.error("User not found with userId: {}", userId);
            return Optional.empty();
        }
    }

    @Transactional
    //@PreAuthorize("#userName == authentication.name")
    public Optional<User> updateUser(final String userName, UserRequest userRequest) {
        Optional<User> optionalUser = userRepository.findByUserName(userName);

        if (optionalUser.isPresent()) {
            updateUser(optionalUser.get(), userRequest);
            return optionalUser;
        } else {
            log.error("User not found with userName: {}", userName);
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
            verifyEmail(user);
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
        return roleIds.stream()
                .map(RoleEnum::getByValue)
                .map(RoleEnum::getName)
                .map(role -> "ROLE_" + role)
                .toList();
    }

    public void verifyEmail(User user) {
        user.setEmailVerified(true);
    }

    @Transactional
    public void verifyEmail(final int userId) {
        User user = getUser(userId);
        verifyEmail(user);
    }

    public void verifyUser(User user) {
        user.setVerified(true);
    }

    @Transactional
    public void verifyUser(final int userId) {
        User user = getUser(userId);
        verifyUser(user);
    }

    public UserResponse buildUserResponse(
            final User user,
            List<RoleEnum> roles,
            List<UserOfficeResponse> userOfficeResponses
    ) {
        List<RoleResponse> roleResponses = roles.stream()
                .map(role -> new RoleResponse(role.getValue(), role.getName(), role.getDisplayName()))
                .toList();

        return UserResponse.builder()
                .userId(user.getId())
                .userName(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .roles(roleResponses)
                .userOffices(userOfficeResponses)
                .build();
    }

    public UserResponse getLoggedInUserResponse() {
        final String userName = userUtil.getUserName();
        final User user = getUserByUserName(userName).orElseThrow();

        var userIdToOfficeResponseMap = getUserIdToOfficeResponseMap(List.of(user));
        List<UserOfficeResponse> userOfficeResponses = userIdToOfficeResponseMap.getOrDefault(user.getId(), List.of());
        return buildUserResponse(user, userUtil.getUserRoles(), userOfficeResponses);
    }

    private Map<Integer, List<UserOfficeResponse>> getUserIdToOfficeResponseMap(List<User> users) {
        List<Integer> userIds = users.stream()
                .map(User::getId)
                .toList();
        return userOfficeService.getUserOfficeResponses(userIds)
                .stream()
                .collect(Collectors.groupingBy(UserOfficeResponse::getUserId));
    }

    private Map<Integer, List<RoleEnum>> getUserIdToRoleEnumMap(List<User> users) {
        List<Integer> userIds = users.stream()
                .map(User::getId)
                .toList();
        List<UserRole> roles = userRoleService.retrieveRoles(userIds);
        return roles.stream()
                .collect(
                        Collectors.groupingBy(
                                UserRole::getUserId,
                                Collectors.mapping(
                                        userRole -> RoleEnum.getByValue(userRole.getRoleId()),
                                        Collectors.toList()
                                )
                        )
                );
    }

    public List<UserResponse> buildUserResponseList(List<User> users) {
        Map<Integer, List<UserOfficeResponse>> userIdToOfficeResponseMap = getUserIdToOfficeResponseMap(users);
        Map<Integer, List<RoleEnum>> userIdToRoleEnumMap = getUserIdToRoleEnumMap(users);

        return users.stream()
                .map(user -> buildUserResponse(user,
                        userIdToRoleEnumMap.get(user.getId()),
                        userIdToOfficeResponseMap.get(user.getId()))
                )
                .toList();
    }

    private void convertUserFromRequest(User user, UserRequest request) {
        if (request.getUserName() != null)
            user.setUserName(request.getUserName());
        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        if (request.getName() != null)
            user.setName(request.getName());
        if (request.getRawPassword() != null)
            user.setPassword(passwordService.encode(request.getRawPassword()));
        if (request.getIsEmailVerified() != null)
            user.setEmailVerified(request.getIsEmailVerified());
        if (request.getIsVerified() != null)
            user.setVerified(request.getIsVerified());
    }

    private void updateRole(final int userId, UserRequest userRequest) {
        if (userRequest.getRole() != null)
            userRoleService.assign(userId, userRequest.getRole());
    }

    private Optional<User> saveUserByAdmin(User user, UserRequest userRequest) {
        try {
            convertUserFromRequest(user, userRequest);
            user = userRepository.save(user);
            updateRole(user.getId(), userRequest);
            return Optional.of(user);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Transactional
    //@PreAuthorize("hasRole('ADMIN')")
    public Optional<User> createUserByAdmin(UserRequest userRequest) {
        return saveUserByAdmin(new User(), userRequest);
    }

    @Transactional
    //@PreAuthorize("hasRole('ADMIN')")
    public Optional<User> updateUserByAdmin(final int userId, UserRequest userRequest) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            return saveUserByAdmin(optionalUser.get(), userRequest);
        } else {
            log.error("User not found with id: " + userId);
            return Optional.empty();
        }
    }

    // util
    private UserDropdown buildDropdown(final User user, List<UserOfficeResponse> userOfficeResponses) {
        Optional<UserOfficeResponse> userOffice = userOfficeResponses.isEmpty()
                ? Optional.empty()
                : Optional.of(userOfficeResponses.getFirst());

        return UserDropdown.builder()
                .username(user.getUsername())
                .name(user.getName())
                .designation(userOffice.map(UserOfficeResponse::getDesignation).orElse(null))
                .office(userOffice.map(UserOfficeResponse::getOffice).orElse(null))
                .company(userOffice.map(UserOfficeResponse::getCompany).orElse(null))
                .build();
    }

    public List<UserDropdown> getUserDropdowns() {
        String me = userUtil.getUserName();
        List<User> verifiedUsers = findAllVerifiedUsers().stream()
                .sorted(Comparator.comparing(u -> !u.getUsername().equals(me)))
                .toList();

        Predicate<User> isAdmin = user -> Constants.ADMIN.equals(user.getUsername());
        List<User> verifiedUsersExceptAdmin = verifiedUsers.stream()
                .filter(user -> !isAdmin.test(user))
                .toList();
        var userIdToOfficeResponseMap = getUserIdToOfficeResponseMap(verifiedUsersExceptAdmin);

        return verifiedUsersExceptAdmin.stream()
                .map(user -> buildDropdown(
                        user,
                        userIdToOfficeResponseMap.getOrDefault(user.getId(), List.of())
                ))
                .toList();
    }

}
