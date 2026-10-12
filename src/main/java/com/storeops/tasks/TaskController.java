package com.storeops.tasks;

import com.storeops.tasks.dto.TaskDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<TaskDto>> list() {
        return ResponseEntity.ok(service.list());
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(@RequestBody TaskDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> get(@PathVariable String taskId) {
        return ResponseEntity.ok(service.get(taskId));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskDto> patch(@PathVariable String taskId, @RequestBody TaskDto patch) {
        return ResponseEntity.ok(service.patch(taskId, patch));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable String taskId) {
        service.delete(taskId);
        return ResponseEntity.noContent().build();
    }
}
