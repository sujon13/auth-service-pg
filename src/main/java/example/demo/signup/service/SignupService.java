package example.demo.signup.service;

import example.demo.model.Role;
import example.demo.model.UserRequest;
import example.demo.repository.UserRepository;
import example.demo.service.RoleService;
import example.demo.service.UserRoleService;
import example.demo.service.auth.PasswordService;
import example.demo.signup.enums.OtpValidation;
import example.demo.signup.model.*;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SignupService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final PasswordService passwordService;
    private final OtpService otpService;


    @PreAuthorize("hasRole('ADMIN')")
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

    public boolean isUserNameAlreadyExist(String userName) {
        return userRepository.existsByUserName(userName);
    }

    public Map<String, String> checkUserNameAndPasswordUniqueness(SignupRequest signupRequest) {
        Map<String, String> errorMap = new HashMap<>();
        Optional<User> optionalUser = userRepository.findByUserNameOrEmail(signupRequest.getUserName(), signupRequest.getEmail());
        if (optionalUser.isEmpty()) {
            return errorMap;
        }
        User user = optionalUser.get();

        if (user.getUsername().equals(signupRequest.getUserName())) {
            errorMap.put("userName", "username already exists");
        }
        if (user.getEmail().equals(signupRequest.getEmail())) {
            errorMap.put("email", "email already exists");
        }
        return errorMap;
    }

    private User createUserFromRequest(SignupRequest signupRequest) {
        User user = new User();
        user.setUserName(signupRequest.getUserName());
        user.setEmail(signupRequest.getEmail());
        user.setName(signupRequest.getName());
        user.setPassword(passwordService.encode(signupRequest.getRawPassword()));
        user.setCreatedBy(user.getUsername());
        return user;
    }

    private Optional<User> saveUser(User user) {
        try {
            userRepository.save(user);
            return Optional.of(user);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return Optional.empty();
        }
    }

    private SignupResponse createSignupResponse(User user) {
        return SignupResponse.builder()
                .userId(user.getId())
                .userName(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public Optional<SignupResponse> signup(SignupRequest signupRequest) {
        User user = createUserFromRequest(signupRequest);
        return saveUser(user)
                .map(this::createSignupResponse);
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

    public List<? extends GrantedAuthority> getRolesOfUser(User user) {
        List<Integer> roleIds = userRoleService.retrieveRoleIds(user.getId());
        return roleService.retrieveRoles(roleIds)
                .stream()
                .map(Role::getName)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }

    @Transactional
    public Optional<OtpResponse> sendOtp(final int userId, final String email) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        User user = optionalUser.get();
        if (user.getId().equals(userId) && user.getEmail().equals(email)) {
            return Optional.of(otpService.sendOtp(userId, email));
        } else {
            return Optional.empty();
        }
    }

    private void makeUserVerified(User user) {
        user.setVerified(true);
    }

    @Transactional
    public OtpValidation verifyOtp(final OtpRequest otpRequest) {
        OtpValidation otpValidation = otpService.validateOtp(otpRequest);

        if (otpValidation.doesNotMatch()) {
            return otpValidation;
        }

        Optional<User> optionalUser = userRepository.findById(otpRequest.getUserId());
        if (optionalUser.isPresent()) {
            this.makeUserVerified(optionalUser.get());
            otpService.makeOtpUsed(otpRequest.getId());
        } else {
            otpValidation = OtpValidation.USER_NOT_FOUND;
        }
        return otpValidation;
    }
}
