package com.example.client.service;

import com.example.client.entity.ClientTask;
import com.example.client.model.TaskRequestDTO;

public interface ClientService {

    ClientTask createTask(Long userId, TaskRequestDTO req);

    ClientTask getTaskStatus(Long clientTaskId);

    ClientTask refreshTaskProgress(Long clientTaskId);
}