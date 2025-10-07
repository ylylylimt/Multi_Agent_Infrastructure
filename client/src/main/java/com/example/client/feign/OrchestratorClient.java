package com.example.client.feign;

import com.example.client.model.TaskRequestDTO;
import com.example.client.model.OrchestratorResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "orchestrator", url = "${orchestrator.base-url}")
public interface OrchestratorClient {
    
    @PostMapping("/api/tasks")
    OrchestratorResponseDTO createTask(@RequestHeader("X-Correlation-ID") String correlationId, 
                                       @RequestBody TaskRequestDTO request);
    
    @GetMapping("/api/tasks/{taskId}")
    OrchestratorResponseDTO getTaskStatus(@PathVariable("taskId") Long taskId);
    
    @GetMapping("/api/tasks/{taskId}/status")
    OrchestratorResponseDTO getTaskProgress(@PathVariable("taskId") Long taskId);
}