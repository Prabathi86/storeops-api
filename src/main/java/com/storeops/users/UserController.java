package com.storeops.users;

import com.storeops.users.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<UserDto>> list() {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> get(@PathVariable String userId) {
        return ResponseEntity.ok(service.get(userId));
    }
}
