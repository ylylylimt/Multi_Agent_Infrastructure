package com.example.agentorch.controller;

import com.example.agentorch.model.Message;
import com.example.agentorch.service.impl.MessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageServiceImpl messageServiceImpl;

    @GetMapping("/inbox")
    public List<Message> inbox(@RequestParam Long agentId) {
        return messageServiceImpl.inbox(agentId);
    }
}
