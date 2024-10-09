package example.demo.controller;

import example.demo.model.UserResponseDto;
import example.demo.service.ApiCallService;
import example.demo.service.auth.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HelloController {
    private final ApiCallService apiCallService;
    private final SecurityService securityService;

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello Nusrat! ";
    }

    @GetMapping("/secure")
    public Double securityTest() {
        return securityService.getRandom();
    }

    @GetMapping("/test")
    public ResponseEntity<UserResponseDto> paiTest() {
        UserResponseDto userResponseDto = apiCallService.handleApiCall();
        return ResponseEntity.ok(userResponseDto);
    }
}
