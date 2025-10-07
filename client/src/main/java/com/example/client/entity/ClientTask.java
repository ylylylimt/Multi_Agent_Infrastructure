package com.example.client.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "client_tasks")
@Data
public class ClientTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orchestratorTaskId;
    private String name;
    private String payload;
    private String status;
    private int progress = 0;
    private Instant createdAt;
    private String correlationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
}