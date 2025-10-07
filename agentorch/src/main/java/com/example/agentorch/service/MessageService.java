package com.example.agentorch.service;

import com.example.agentorch.model.Message;
import java.util.List;

public interface MessageService {

    Message send(Long fromAgentId, Long toAgentId, String payload);

    List<Message> inbox(Long agentId);

}
