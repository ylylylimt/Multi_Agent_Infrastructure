package com.example.agentorch.service;

import com.example.agentorch.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskService {

    Task createTask(Task t, List<Long> agentIds);

    Optional<Task> get(Long id);

    List<Task> list();

    Task startTask(Long taskId);
    
    Task completeTask(Long taskId);

    Task updateProgress(Long taskId, int progress);

    Task updateStatus(Long taskId, Task.Status status);

}
