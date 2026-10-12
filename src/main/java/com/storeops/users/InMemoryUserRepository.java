package com.storeops.users;

import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> store = new HashMap<>();

    public InMemoryUserRepository() {
        User u1 = new User();
        u1.setId("user-1");
        u1.setName("Alice");
        u1.setStoreId("store-1");
        u1.setRole(StaffRole.STORE_MANAGER);
        UserProfile p1 = new UserProfile();
        p1.setDepartment("Grocery");
        p1.setEmail("alice@example.com");
        u1.setProfile(p1);
        store.put(u1.getId(), u1);
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }
}
