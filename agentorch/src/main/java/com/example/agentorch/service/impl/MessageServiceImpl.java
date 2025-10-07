package com.example.agentorch.service.impl;

import com.example.agentorch.model.Message;
import com.example.agentorch.repository.MessageRepository;
import com.example.agentorch.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository repo;

    @Override
    public Message send(Long fromAgentId, Long toAgentId, String payload) {
        Message m = Message.builder()
                .fromAgentId(fromAgentId)
                .toAgentId(toAgentId)
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .status("SENT")
                .build();
        return repo.save(m);
    }

    @Override
    public List<Message> inbox(Long agentId) {
        return repo.findByToAgentIdOrderByCreatedAtDesc(agentId);
    }

}
