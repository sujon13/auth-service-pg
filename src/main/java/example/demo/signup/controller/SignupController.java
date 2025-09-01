package example.demo.signup.controller;

import example.demo.model.UserRequest;
import example.demo.service.UserService;
import example.demo.service.auth.PasswordService;
import example.demo.signup.enums.OtpValidation;
import example.demo.signup.model.*;
import example.demo.signup.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/signup")
public class SignupController {
    private final UserService userService;
    private final SignupService signupService;
    private final PasswordService passwordService;


    @GetMapping("/checkUserName")
    public boolean userNameExists(@RequestParam String userName) {
        return signupService.isUserNameAlreadyExist(userName);
    }

    @PostMapping("/{userName}")
    public ResponseEntity<User> updateUser(@PathVariable String userName,
                                           @RequestBody UserRequest userRequest) {

        if (userName == null) {
            log.error("bad request");
            return ResponseEntity.badRequest().build();
        }

        return userService.updateUser(userName, userRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        if (signupService.emailAlreadyExists(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        return signupService.signup(signupRequest)
                .map(signupResponse -> new ResponseEntity<>(signupResponse, HttpStatus.CREATED))
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

    @PostMapping("/send-otp")
    public ResponseEntity<OtpResponse> sendOtp(@Valid @RequestBody OtpSendRequest otpSendRequest) {
        return signupService.sendOtp(otpSendRequest.getUserId(), otpSendRequest.getEmail())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody OtpRequest otpRequest) {
        OtpValidation otpValidation = signupService.verifyOtp(otpRequest);
        String message = otpValidation.getMessage();

        if (OtpValidation.MATCHED.equals(otpValidation)) {
            log.info(message);
            return ResponseEntity.ok(message);
        } else  {
            log.error(message);
            return ResponseEntity.badRequest().body(message);
        }
    }
}
