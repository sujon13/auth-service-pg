package example.demo.controller;

import example.demo.model.UserDropdown;
import example.demo.model.UserRequest;
import example.demo.model.UserResponse;
import example.demo.service.UserService;
import example.demo.signup.model.User;
import example.demo.util.UserUtil;
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
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserUtil userUtil;

    @GetMapping("")
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam List<String> userNames) {
        return ResponseEntity.ok(userService.getUsers(userNames));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(userService.getLoggedInUserResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id,
                                            @RequestParam(value = "user_name", required = false) String name,
                                            @RequestBody(required = false) UserRequest userRequest) {

        log.info("id: " + id + " name: " + name);
        log.info("user request: " + userRequest);

        return ResponseEntity.ok(userService.getUser(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable final int id,
                                           @RequestBody UserRequest userRequest) {

        Optional<User> optionalUser = userUtil.isAdmin()
                ? userService.updateUserByAdmin(id, userRequest)
                : userService.updateUser(id, userRequest);
        return optionalUser
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody UserRequest userRequest) {
        Optional<User> optionalUser = userUtil.isAdmin()
                ? userService.createUserByAdmin(userRequest)
                : userService.saveUser(userRequest);
        return optionalUser
                .map(user -> new ResponseEntity<>(user, HttpStatus.CREATED))
                .orElse(ResponseEntity.internalServerError().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (id == null) {
            log.error("bad request");
            return ResponseEntity.badRequest().build();
        }

        try {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/verify")
    public void verifyByAdmin(@PathVariable("id") final int userId) {
        userService.verifyUser(userId);
    }

    @PostMapping("/{id}/verify/email")
    public void verifyEmail(@PathVariable("id") final int userId) {
        userService.verifyEmail(userId);
    }

    @GetMapping("/dropdown")
    public List<UserDropdown> getVerifiedUsers(@RequestParam(value = "assignee", defaultValue = "false") boolean assignee) {
        return userService.getUserDropdowns(assignee);
    }

}
