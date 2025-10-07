package com.example.agentorch.controller;

import com.example.agentorch.repository.AgentRepository;
import com.example.agentorch.repository.MessageRepository;
import com.example.agentorch.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/monitor")
public class MonitoringController {
    private final AgentRepository agentRepo;
    private final TaskRepository taskRepo;
    private final MessageRepository messageRepo;

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        return Map.of(
                "agents", agentRepo.count(),
                "tasks", taskRepo.count(),
                "messages", messageRepo.count()
        );
    }
}
