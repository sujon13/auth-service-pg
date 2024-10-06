package example.demo.service;

import example.demo.model.User;
import example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        user.setUserId(1);
        user.setName("name1");

        when(userRepository.findByUserId(1)).thenReturn(Optional.of(user));

        Optional<User> optionalUser = userService.getUserById(1);

        assertEquals(1, optionalUser.get().getUserId());
        assertEquals("name1", optionalUser.get().getName());
    }
}
