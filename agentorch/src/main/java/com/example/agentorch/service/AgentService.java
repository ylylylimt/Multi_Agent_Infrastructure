package com.example.agentorch.service;

import com.example.agentorch.model.Agent;
import com.example.agentorch.model.Task;
import java.util.List;
import java.util.Optional;

public interface AgentService {

    Agent register(Agent agent);

    List<Agent> list();

    Optional<Agent> get(Long id);

    void delete(Long id);
    
    void dispatchTaskToAgent(Long agentId, Task task);
}
