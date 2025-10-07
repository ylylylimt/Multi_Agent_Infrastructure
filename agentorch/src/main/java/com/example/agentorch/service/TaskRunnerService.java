package com.example.agentorch.service;

import com.example.agentorch.model.Task;
import com.example.agentorch.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskRunnerService {

    private final TaskRepository taskRepository;
    private final TaskService taskService;

    @PostConstruct
    public void init() {
        log.info("TaskRunnerService initialized — background task dispatcher started");
    }

    @Scheduled(fixedDelay = 5000) // every 5 seconds
    @Transactional
    public void runPendingTasks() {
        List<Task> pending = taskRepository.findByStatus(Task.Status.PENDING);
        for (Task task : pending) {
            log.info("Found pending task: {}", task);
            if (dependenciesCompleted(task)) {
                executeTask(task);
            }
        }
    }

    private boolean dependenciesCompleted(Task task) {
        if (task.getDependsOn() == null || task.getDependsOn().isEmpty()) return true;
        return task.getDependsOn().stream()
                .map(taskRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .allMatch(dep -> dep.getStatus() == Task.Status.COMPLETED);
    }

    private void executeTask(Task task) {
        log.info("Executing task: {}", task.getId());
        taskService.startTask(task.getId());
    }
}