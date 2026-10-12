package com.storeops.users;

import com.storeops.common.NotFoundError;
import com.storeops.users.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    UserRepository repo;
    UserService service;

    @BeforeEach
    void setup() {
        repo = new InMemoryUserRepository();
        service = new UserService(repo);
    }

    @Test
    void getUser_ok() {
        UserDto dto = service.get("user-1");
        assertEquals("user-1", dto.userId);
    }

    @Test
    void getUser_notFound() {
        assertThrows(NotFoundError.class, () -> service.get("missing"));
    }

    @Test
    void listUsers_ok() {
        assertFalse(service.list().isEmpty());
    }
}
