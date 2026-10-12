package com.storeops.projects;

import com.storeops.projects.dto.ProjectDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService service;

    public ProjectController(ProjectService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> list(@RequestHeader("X-Store-Id") String storeId) {
        return ResponseEntity.ok(service.listForStore(storeId));
    }

    @PostMapping
    public ResponseEntity<ProjectDto> create(@RequestBody ProjectDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<ProjectDto> addMember(
            @PathVariable String projectId,
            @RequestBody ProjectDto.MemberDto memberDto
    ) {
        return ResponseEntity.ok(service.addMember(projectId, memberDto));
    }
}
