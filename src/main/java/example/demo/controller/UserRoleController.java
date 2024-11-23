package example.demo.controller;

import example.demo.model.UserRole;
import example.demo.model.UserRoleRequest;
import example.demo.service.UserRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/users")
public class UserRoleController {
    private final UserRoleService userRoleService;

    @PostMapping("/{id}/assignRole")
    public UserRole assignRole(@PathVariable("id") final int userId, @Valid @RequestBody final UserRoleRequest request) {
        return userRoleService.assign(request);
    }

}
