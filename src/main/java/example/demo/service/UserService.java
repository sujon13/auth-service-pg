package example.demo.service;

import example.demo.model.User;
import example.demo.model.UserRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Integer userId) {
        return userRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByName(String name) {
        return name == null
                ? Optional.empty()
                : userRepository.findUserByName(name);
    }

    @Transactional
    public Optional<User> updateUser(int userId, UserRequest userRequest) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);

        if (optionalUser.isPresent()) {
            optionalUser.get().setName(userRequest.getName());
            return optionalUser;
        } else {
            log.error("User not found with userId: " + userId);
            return Optional.empty();
        }
    }

    private User createUserFromRequest(UserRequest userRequest) {
        User user = new User();
        user.setUserId(userRequest.getUserId());
        user.setName(userRequest.getName());
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
    public void deleteByUserId(int userId) {
        try {
            userRepository.deleteByUserId(userId);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            log.error(emptyResultDataAccessException.getMessage());
            throw new RuntimeException("not found");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
