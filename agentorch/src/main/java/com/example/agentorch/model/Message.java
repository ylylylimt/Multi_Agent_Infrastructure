package com.example.agentorch.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromAgentId;
    private Long toAgentId;

    @Column(columnDefinition = "text")
    private String payload;

    private LocalDateTime createdAt;

    private String status;
}
