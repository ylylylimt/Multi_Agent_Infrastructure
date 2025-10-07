package com.example.client.controller;

import com.example.client.entity.ClientTask;
import com.example.client.model.TaskRequestDTO;
import com.example.client.model.TaskProgressDTO;
import com.example.client.service.impl.ClientServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/client")
public class ClientController {
    private final ClientServiceImpl service;

    public ClientController(ClientServiceImpl service) {
        this.service = service;
    }

    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@RequestParam Long userId, @RequestBody Map<String, Object> req) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Valid userId is required"));
            }
            if (req == null || req.isEmpty() || !req.containsKey("name")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Request body must contain 'name' field"));
            }

            TaskRequestDTO dto = TaskRequestDTO.builder()
                    .name((String) req.get("name"))
                    .payload(req.get("payload"))
                    .dependsOn((java.util.List<Long>) req.get("dependsOn"))
                    .build();
            
            ClientTask task = service.createTask(userId, dto);
            return getTaskStatus(task.getId());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage(), "timestamp", java.time.Instant.now()));
        }
    }

    @GetMapping("/tasks/{clientTaskId}")
    public ResponseEntity<?> getTaskStatus(@PathVariable Long clientTaskId) {
        try {
            ClientTask task = service.getTaskStatus(clientTaskId);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Task not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage(), "timestamp", java.time.Instant.now()));
        }
    }

    @GetMapping("/tasks/{clientTaskId}/progress")
    public ResponseEntity<?> getTaskProgress(@PathVariable Long clientTaskId) {
        try {
            ClientTask task = service.refreshTaskProgress(clientTaskId);
            return ResponseEntity.ok(TaskProgressDTO.builder()
                    .id(task.getId())
                    .status(task.getStatus())
                    .progress(task.getProgress())
                    .name(task.getName())
                    .build());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Task not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage(), "timestamp", java.time.Instant.now()));
        }
    }
}