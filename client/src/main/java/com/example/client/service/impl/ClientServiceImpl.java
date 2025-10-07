package com.example.client.service.impl;

import com.example.client.entity.ClientTask;
import com.example.client.entity.User;
import com.example.client.feign.OrchestratorClient;
import com.example.client.model.TaskRequestDTO;
import com.example.client.model.OrchestratorResponseDTO;
import com.example.client.repository.ClientTaskRepository;
import com.example.client.repository.UserRepository;
import com.example.client.service.ClientService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Data
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final OrchestratorClient orchestratorClient;
    private final ClientTaskRepository taskRepo;
    private final UserRepository userRepo;

    @Override
    public ClientTask createTask(Long userId, TaskRequestDTO req) {
        try {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            String correlationId = UUID.randomUUID().toString();

            OrchestratorResponseDTO orchResp = orchestratorClient.createTask(correlationId, req);

            if (orchResp == null || orchResp.getId() == null) {
                throw new RuntimeException("Invalid response from orchestrator: " + orchResp);
            }

            ClientTask task = new ClientTask();
            task.setUser(user);
            task.setOrchestratorTaskId(orchResp.getId());
            task.setName(req.getName());
            task.setPayload(req.getPayload() != null ? req.getPayload().toString() : null);
            task.setCreatedAt(Instant.now());
            task.setStatus("PENDING");
            task.setCorrelationId(correlationId);
            return taskRepo.save(task);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create task: " + e.getMessage(), e);
        }
    }

    @Override
    public ClientTask getTaskStatus(Long clientTaskId) {
        try {
            ClientTask task = taskRepo.findById(clientTaskId)
                    .orElseThrow(() -> new RuntimeException("Task not found with ID: " + clientTaskId));
            
            try {
                OrchestratorResponseDTO orchStatus = orchestratorClient.getTaskStatus(task.getOrchestratorTaskId());
                
                if (orchStatus != null && orchStatus.getStatus() != null) {
                    task.setStatus(orchStatus.getStatus());
                    return taskRepo.save(task);
                }
            } catch (Exception orchError) {
                System.out.println("Warning: Could not fetch status from orchestrator: " + orchError.getMessage());
            }

            return task;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get task status: " + e.getMessage(), e);
        }
    }

    @Override
    public ClientTask refreshTaskProgress(Long clientTaskId) {
        ClientTask task = taskRepo.findById(clientTaskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + clientTaskId));
        
        OrchestratorResponseDTO orchProgress = orchestratorClient.getTaskProgress(task.getOrchestratorTaskId());
        
        if (orchProgress != null) {
            if (orchProgress.getStatus() != null) {
                task.setStatus(orchProgress.getStatus());
            }
            if (orchProgress.getProgress() != null) {
                task.setProgress(orchProgress.getProgress());
            }
        }
        
        return taskRepo.save(task);
    }
}