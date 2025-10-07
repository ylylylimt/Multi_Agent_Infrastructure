package com.example.client.repository;

import com.example.client.entity.ClientTask;
import com.example.client.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClientTaskRepository extends JpaRepository<ClientTask, Long> {
    List<ClientTask> findByUser(User user);
}