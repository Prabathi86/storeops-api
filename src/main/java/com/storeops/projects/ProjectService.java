package com.storeops.projects;

import com.storeops.common.EventBus;
import com.storeops.common.NotFoundError;
import com.storeops.common.ValidationError;
import com.storeops.projects.dto.ProjectDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private final ProjectRepository repo;
    private final EventBus bus;

    public ProjectService(ProjectRepository repo, EventBus bus) {
        this.repo = repo;
        this.bus = bus;
    }

    public ProjectDto create(ProjectDto dto) {
        if (dto.projectName == null || dto.projectName.isBlank()) {
            throw new ValidationError("PROJECT_NAME_REQUIRED", "Project name is required");
        }
        Project p = toDomain(dto);
        Project saved = repo.save(p);
        bus.publish("projects.created", saved);
        return toDto(saved);
    }

    public List<ProjectDto> listForStore(String storeId) {
        return repo.findAll().stream()
                .filter(p -> storeId.equals(p.getStoreId()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProjectDto addMember(String projectId, ProjectDto.MemberDto memberDto) {
        Project p = repo.findById(projectId)
                .orElseThrow(() -> new NotFoundError("PROJECT_NOT_FOUND", "Project not found"));
        ProjectMember m = new ProjectMember();
        m.setUserId(memberDto.userId);
        m.setRole(memberDto.role);
        p.getMembers().add(m);
        Project saved = repo.save(p);
        bus.publish("projects.memberAdded", memberDto.userId);
        return toDto(saved);
    }

    private Project toDomain(ProjectDto dto) {
        Project p = new Project();
        p.setId(dto.projectId);
        p.setName(dto.projectName);
        p.setStoreId(dto.storeId);
        if (dto.members != null) {
            for (ProjectDto.MemberDto md : dto.members) {
                ProjectMember m = new ProjectMember();
                m.setUserId(md.userId);
                m.setRole(md.role);
                p.getMembers().add(m);
            }
        }
        return p;
    }

    private ProjectDto toDto(Project p) {
        ProjectDto dto = new ProjectDto();
        dto.projectId = p.getId();
        dto.projectName = p.getName();
        dto.storeId = p.getStoreId();
        dto.members = p.getMembers().stream().map(m -> {
            ProjectDto.MemberDto md = new ProjectDto.MemberDto();
            md.userId = m.getUserId();
            md.role = m.getRole();
            return md;
        }).collect(Collectors.toList());
        return dto;
    }
}
