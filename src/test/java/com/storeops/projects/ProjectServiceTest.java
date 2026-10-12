package com.storeops.projects;

import com.storeops.common.EventBus;
import com.storeops.common.NotFoundError;
import com.storeops.projects.dto.ProjectDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest {

    ProjectRepository repo;
    EventBus bus;
    ProjectService service;

    @BeforeEach
    void setup() {
        repo = new InMemoryProjectRepository();
        bus = (topic, payload) -> {};
        service = new ProjectService(repo, bus);
    }

    @Test
    void createProject_ok() {
        ProjectDto dto = new ProjectDto();
        dto.projectName = "Planogram Reset";
        dto.storeId = "store-1";

        ProjectDto created = service.create(dto);
        assertNotNull(created.projectId);
    }

    @Test
    void addMember_ok() {
        ProjectDto dto = new ProjectDto();
        dto.projectName = "Audit Prep";
        dto.storeId = "store-1";
        ProjectDto created = service.create(dto);

        ProjectDto.MemberDto m = new ProjectDto.MemberDto();
        m.userId = "user-1";
        m.role = ProjectRole.ASSOCIATE;

        ProjectDto updated = service.addMember(created.projectId, m);
        assertEquals(1, updated.members.size());
    }

    @Test
    void projectNotFound() {
        ProjectDto.MemberDto m = new ProjectDto.MemberDto();
        m.userId = "user-1";
        m.role = ProjectRole.ASSOCIATE;

        assertThrows(NotFoundError.class, () -> service.addMember("missing", m));
    }
}
