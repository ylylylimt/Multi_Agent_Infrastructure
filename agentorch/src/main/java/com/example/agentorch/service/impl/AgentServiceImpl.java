package com.example.agentorch.service.impl;

import com.example.agentorch.model.Agent;
import com.example.agentorch.model.Task;
import com.example.agentorch.repository.AgentRepository;
import com.example.agentorch.service.AgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentServiceImpl implements AgentService {
    private final AgentRepository repository;

    @Override
    public Agent register(Agent agent) {
        if (agent.getStatus() == null) agent.setStatus("ACTIVE");
        return repository.save(agent);
    }

    @Override
    public List<Agent> list() { return repository.findAll(); }

    @Override
    public Optional<Agent> get(Long id) { return repository.findById(id); }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
    
    @Override
    public void dispatchTaskToAgent(Long agentId, Task task) {
        log.info("Dispatching task {} to agent {}", task.getId(), agentId);
        // Implementation for task dispatch logic
    }
}
