package example.demo.service;

import example.demo.signup.model.User;
import example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    public void testGetUserById_success() {
        User user = new User();
        String userName = "name1@gmail.com";
        user.setUserName(userName);
        user.setName("name1");

        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));

        Optional<User> optionalUser = userService.getUserByUserName(userName);

        assertEquals("name1@gmail.com", optionalUser.get().getUserName());
        assertEquals("name1", optionalUser.get().getName());
    }
}
