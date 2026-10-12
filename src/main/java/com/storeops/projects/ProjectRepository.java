package com.storeops.projects;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(String id);
    List<Project> findAll();
}
