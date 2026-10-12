package com.storeops.tasks;

import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class InMemoryTaskRepository implements TaskRepository {
    private final Map<String, Task> store = new HashMap<>();

    @Override
    public Task save(Task task) {
        if (task.getId() == null || task.getId().isBlank()) {
            task.setId(UUID.randomUUID().toString());
        }
        store.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(String id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public List<Task> findAll() { return new ArrayList<>(store.values()); }

    @Override
    public void deleteById(String id) { store.remove(id); }
}
