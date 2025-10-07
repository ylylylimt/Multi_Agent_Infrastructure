package com.example.agentorch.controller;

import com.example.agentorch.model.Task;
import com.example.agentorch.service.impl.TaskServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskServiceImpl taskServiceImpl;

    @GetMapping
    public ResponseEntity<List<Task>> list() {
        return ResponseEntity.ok(taskServiceImpl.list());
    }
    
    @GetMapping("/get")
    public ResponseEntity<Task> getById(@RequestParam Long id) {
        Optional<Task> task = taskServiceImpl.get(id);
        return task.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Task> create(@RequestBody CreateTaskRequest r) {
        Task t = Task.builder()
                .name(r.name)
                .payload(r.payload)
                .parallel(r.parallel)
                .agentIds(r.assignedAgentIds)
                .dependsOn(r.dependsOn)
                .build();
        Task created = taskServiceImpl.createTask(t, r.assignedAgentIds);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping("/start")
    public ResponseEntity<Task> start(@RequestParam Long id) {
        Task t = taskServiceImpl.startTask(id);
        return ResponseEntity.ok(t);
    }
    
    @PostMapping("/complete")
    public ResponseEntity<Task> complete(@RequestParam Long id) {
        Task t = taskServiceImpl.completeTask(id);
        return ResponseEntity.ok(t);
    }

    @PutMapping("/{id}/progress")
    public ResponseEntity<Task> updateProgress(@PathVariable Long id, @RequestParam int progress) {
        Task t = taskServiceImpl.updateProgress(id, progress);
        return ResponseEntity.ok(t);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Task> updateStatus(@PathVariable Long id, @RequestParam Task.Status status) {
        Task t = taskServiceImpl.updateStatus(id, status);
        return ResponseEntity.ok(t);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<TaskStatusResponse> getTaskStatus(@PathVariable Long id) {
        Optional<Task> task = taskServiceImpl.get(id);
        if (task.isPresent()) {
            Task t = task.get();
            return ResponseEntity.ok(new TaskStatusResponse(t.getId(), t.getStatus(), t.getProgress()));
        }
        return ResponseEntity.notFound().build();
    }

    public static class CreateTaskRequest {
        public String name;
        public String payload;
        public boolean parallel;
        public List<Long> assignedAgentIds;
        public List<Long> dependsOn;
    }

    public static class TaskStatusResponse {
        public Long id;
        public Task.Status status;
        public int progress;

        public TaskStatusResponse(Long id, Task.Status status, int progress) {
            this.id = id;
            this.status = status;
            this.progress = progress;
        }
    }
}

