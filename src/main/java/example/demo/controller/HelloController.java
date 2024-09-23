package example.demo.controller;

import example.demo.model.User;
import example.demo.model.UserRequest;
import example.demo.model.UserResponseDto;
import example.demo.service.ApiCallService;
import example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HelloController {
    private final UserService userService;
    private final ApiCallService apiCallService;

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello Nusrat!";
    }

    @GetMapping("/test")
    public ResponseEntity<UserResponseDto> paiTest() {
        UserResponseDto userResponseDto = apiCallService.handleApiCall();
        return ResponseEntity.ok(userResponseDto);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users")
    //@GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Integer userId,
                                            @RequestParam(value = "user_name", required = false) String name,
                                            @RequestBody(required = false) UserRequest userRequest) {

        log.info("userId: " + userId + " name: " + name);
        log.info("user request: " + userRequest);
        Optional<User> user = userService.getUserById(userId);

        return user
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        //.orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Integer userId,
                                           @RequestBody UserRequest userRequest) {

        if (userId == null) {
            log.error("bad request");
            return ResponseEntity.badRequest().build();
        }

        return userService.updateUser(userId, userRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody UserRequest userRequest) {
        return userService.saveUser(userRequest)
                .map(user -> new ResponseEntity<>(user, HttpStatus.CREATED))
                .orElse(ResponseEntity.internalServerError().build());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Integer userId) {
        if (userId == null) {
            log.error("bad request");
            return ResponseEntity.badRequest().build();
        }

        try {
            userService.deleteByUserId(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
