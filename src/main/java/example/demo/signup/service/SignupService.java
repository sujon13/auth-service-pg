package example.demo.signup.service;

import example.demo.model.UserRequest;
import example.demo.repository.UserRepository;
import example.demo.service.UserRoleService;
import example.demo.service.UserService;
import example.demo.service.auth.PasswordService;
import example.demo.signup.enums.OtpValidation;
import example.demo.signup.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
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
public class SignupService {
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final PasswordService passwordService;
    private final OtpService otpService;
    private final UserService userService;


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

    public boolean emailAlreadyExists(String email) {
       return userRepository.existsByEmail(email);
    }

    private String extractUserNameFromEmail(String email) {
        return email.split("@")[0];
    }

    private User createUserFromRequest(SignupRequest signupRequest) {
        User user = new User();
        //user.setUserName(extractUserNameFromEmail(signupRequest.getEmail()));
        user.setEmail(signupRequest.getEmail());
        user.setName(signupRequest.getName());
        user.setPassword(passwordService.encode(signupRequest.getRawPassword()));
        //user.setCreatedBy(user.getUsername());
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
        User newUser = createUserFromRequest(signupRequest);
        return saveUser(newUser)
                .stream()
                .map(this::createSignupResponse)
                .findAny();
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

    @Transactional
    public OtpValidation verifyOtp(final OtpRequest otpRequest) {
        OtpValidation otpValidation = otpService.validateOtp(otpRequest);

        if (otpValidation.doesNotMatch()) {
            return otpValidation;
        }

        Optional<User> optionalUser = userRepository.findById(otpRequest.getUserId());
        if (optionalUser.isPresent()) {
            userService.verifyEmail(optionalUser.get());
            otpService.makeOtpUsed(otpRequest.getId());
        } else {
            otpValidation = OtpValidation.USER_NOT_FOUND;
        }
        return otpValidation;
    }
}
