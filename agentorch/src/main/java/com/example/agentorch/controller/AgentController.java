package com.example.agentorch.controller;

import com.example.agentorch.model.Agent;
import com.example.agentorch.service.impl.AgentServiceImpl;
import com.example.agentorch.service.impl.MessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/agents")
public class AgentController {
    private final AgentServiceImpl agentServiceImpl;
    private final MessageServiceImpl messageServiceImpl;

    @GetMapping
    public List<Agent> getAllAgents() { return agentServiceImpl.list(); }

    @PostMapping
    public Agent createAgent(@RequestBody Agent agent) { return agentServiceImpl.register(agent); }

    @GetMapping("/get")
    public ResponseEntity<Agent> getAgent(@RequestParam Long id) {
        return agentServiceImpl.get(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAgent(@RequestParam Long id) {
        agentServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/post")
    public ResponseEntity<Void> sendMessage(@RequestParam Long id, @RequestBody SendMessageRequest request) {
        messageServiceImpl.send(id, request.toAgentId, request.payload);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/inbox")
    public ResponseEntity<?> getInbox(@RequestParam Long id) {
        return ResponseEntity.ok(messageServiceImpl.inbox(id));
    }

    public static class SendMessageRequest {
        public Long toAgentId;
        public String payload;
    }
}
