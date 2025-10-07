package com.example.agentorch.service.impl;

import com.example.agentorch.model.Agent;
import com.example.agentorch.model.Task;
import com.example.agentorch.repository.AgentRepository;
import com.example.agentorch.repository.TaskRepository;
import com.example.agentorch.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final AgentRepository agentRepository;
    private final MessageServiceImpl messageServiceImpl;

    @Override
    public Task createTask(Task t, List<Long> agentIds) {
        Set<Agent> assigned = new HashSet<>();
        if (agentIds != null && !agentIds.isEmpty()) {
            for (Long id : agentIds) {
                agentRepository.findById(id).ifPresent(assigned::add);
            }
            t.setAgentIds(agentIds);
        } else {
            t.setAgentIds(null);
        }
        // assignedAgents is now transient - no DB persistence
        t.setStatus(Task.Status.PENDING);
        t.setCreatedAt(LocalDateTime.now());
        return taskRepository.save(t);
    }

    @Override
    public Optional<Task> get(Long id) { return taskRepository.findById(id); }

    @Override
    public List<Task> list() { return taskRepository.findAll(); }

    @Override
    public Task startTask(Long taskId) {
        Task t = taskRepository.findById(taskId).orElseThrow();

        if (t.getDependsOn() != null && !t.getDependsOn().isEmpty()) {
            for (Long depId : t.getDependsOn()) {
                Task dep = taskRepository.findById(depId).orElseThrow(
                    () -> new RuntimeException("Dependency task " + depId + " not found"));
                if (dep.getStatus() != Task.Status.COMPLETED) {
                    throw new RuntimeException("Cannot start task " + taskId + ": dependency " + depId + " not completed");
                }
            }
        }

        // Populate assignedAgents from agentIds if not already set
        if (t.getAssignedAgents() == null && t.getAgentIds() != null && !t.getAgentIds().isEmpty()) {
            Set<Agent> assigned = new HashSet<>();
            for (Long id : t.getAgentIds()) {
                agentRepository.findById(id).ifPresent(assigned::add);
            }
            t.setAssignedAgents(assigned);
        }

        t.setStatus(Task.Status.RUNNING);
        if (t.getProgress() == 0) {
            t.setProgress(1);
        }
        t.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(t);

        // Only proceed with messaging if we have assigned agents
        if (t.getAssignedAgents() != null && !t.getAssignedAgents().isEmpty()) {
            if (t.isParallel()) {
                for (Agent a : t.getAssignedAgents()) {
                    messageServiceImpl.send(0L, a.getId(), "TASK:" + t.getId() + "|" + t.getPayload());
                }
            } else {
                t.getAssignedAgents().stream().findFirst().ifPresent(a -> {
                    messageServiceImpl.send(0L, a.getId(), "TASK:" + t.getId() + "|" + t.getPayload());
                });
            }
        }

        return t;
    }

    @Override
    public Task completeTask(Long taskId) {
        Task t = taskRepository.findById(taskId).orElseThrow();
        t.setStatus(Task.Status.COMPLETED);
        t.setProgress(100);
        t.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(t);
    }

    @Override
    public Task updateProgress(Long taskId, int progress) {
        Task t = taskRepository.findById(taskId).orElseThrow();
        int clampedProgress = Math.max(0, Math.min(100, progress));
        t.setProgress(clampedProgress);
        
        if (clampedProgress == 100) {
            t.setStatus(Task.Status.COMPLETED);
        }
        
        t.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(t);
    }

    @Override
    public Task updateStatus(Long taskId, Task.Status status) {
        Task t = taskRepository.findById(taskId).orElseThrow();
        t.setStatus(status);
        
        if (status == Task.Status.COMPLETED) {
            t.setProgress(100);
        } else if (status == Task.Status.RUNNING && t.getProgress() == 0) {
            t.setProgress(1);
        }
        
        t.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(t);
    }
}
