package com.storeops.users;

import com.storeops.common.NotFoundError;
import com.storeops.users.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) { this.repo = repo; }

    public UserDto get(String id) {
        User u = repo.findById(id)
                .orElseThrow(() -> new NotFoundError("USER_NOT_FOUND", "User not found"));
        return toDto(u);
    }

    public List<UserDto> list() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private UserDto toDto(User u) {
        UserDto dto = new UserDto();
        dto.userId = u.getId();
        dto.userName = u.getName();
        dto.storeId = u.getStoreId();
        dto.staffRole = u.getRole();
        if (u.getProfile() != null) {
            dto.department = u.getProfile().getDepartment();
            dto.email = u.getProfile().getEmail();
        }
        return dto;
    }
}
