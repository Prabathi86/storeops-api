package com.storeops.projects;

import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class InMemoryProjectRepository implements ProjectRepository {
    private final Map<String, Project> store = new HashMap<>();

    @Override
    public Project save(Project project) {
        if (project.getId() == null || project.getId().isBlank()) {
            project.setId(UUID.randomUUID().toString());
        }
        store.put(project.getId(), project);
        return project;
    }

    @Override
    public Optional<Project> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Project> findAll() {
        return new ArrayList<>(store.values());
    }
}
