package com.example.agentorch.service;

import com.example.agentorch.model.Task;
import com.example.agentorch.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TaskProgressSimulator {

    private final TaskRepository taskRepository;
    private final Random random = new Random();

    @Scheduled(fixedRate = 5000)
    public void simulateProgress() {
        List<Task> runningTasks = taskRepository.findByStatus(Task.Status.RUNNING);

        for (Task task : runningTasks) {
            int increment = random.nextInt(15) + 5; // +5–20%
            int newProgress = Math.min(100, task.getProgress() + increment);
            task.setProgress(newProgress);

            if (newProgress >= 100) {
                task.setStatus(Task.Status.COMPLETED);
            }

            taskRepository.save(task);
            System.out.println("[Simulator] Task " + task.getId() +
                    " -> " + task.getProgress() + "% (" + task.getStatus() + ")");
        }
    }
}