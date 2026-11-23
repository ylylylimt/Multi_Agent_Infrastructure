# Multi_Agent_Infrastructure
Infrastructure for Multi-Agent Systems using Spring Boot, Docker, image optimization

---
## Overview

### **1. Orchestrator Service** (Port **8989**)

Handles agent creation, task coordination, inter-agent communication, and system monitoring.

#### **Agent Management**

* `POST /api/agents` – Create agents with capabilities
* `GET /api/agents` – List all agents
* `GET /api/agents/get?id={id}` – Get specific agent
* `DELETE /api/agents/delete?id={id}` – Delete agent

#### **Task Management**

* `POST /api/tasks` – Create tasks with dependencies and agent assignments
* `GET /api/tasks` – List all tasks
* `GET /api/tasks/get?id={id}` – Get specific task
* `POST /api/tasks/start?id={id}` – Start task execution
* `POST /api/tasks/complete?id={id}` – Mark task as completed

#### **Messaging**

* `POST /api/agents/post?id={fromId}` – Send messages between agents
* `GET /api/agents/inbox?id={id}` – Retrieve agent inbox

#### **Monitoring**

* `GET /api/monitor/summary` – View system statistics (agents, tasks, messages)

---

### **2. Client Service** (Port **8990**)

Provides a user-facing interface for managing users and delegating tasks to the Orchestrator.

#### **User Management**

* `POST /client/users` – Create users
* `GET /client/users` – List all users

#### **Task Management**

* `POST /client/tasks?userId={id}` – Create client tasks (forwarded to Orchestrator)
* `GET /client/tasks/{clientTaskId}` – Check task status

---

## 🔗 Feign Client Integration

The **FeignClient** bridges the Client and Orchestrator services for service-to-service communication.

```java
@FeignClient(name = "orchestrator", url = "${orchestrator.base-url}")
public interface OrchestratorClient {
    TaskResponse createTask(TaskRequest request);
    TaskStatus getTaskStatus(Long id);
}
```

### **Workflow**

* Client receives a task request and forwards it to the Orchestrator via `OrchestratorClient`.
* Orchestrator executes and tracks the task.
* Client maintains a local reference and updates status from Orchestrator.

### **Key Features**

* **Decoupled Services** – No shared databases
* **Resilience** – Fallback handling when Orchestrator is down
* **Correlation** – Client tasks mapped to Orchestrator tasks
* **Scalability** – Feign supports multiple Orchestrator instances

---

## 🛠️ Tech Stack

* **Java 17**
* **Spring Boot** (REST, Feign, Web, Validation)
* **Maven**
* **JSON-based REST APIs**
* **Microservice Architecture**

---

## 🚀 Getting Started

```bash
# Start Orchestrator
cd agentorch
mvn spring-boot:run

# Start Client
cd client
mvn spring-boot:run
```

Access services:

* Orchestrator → [http://localhost:8989](http://localhost:8989)
* Client → [http://localhost:8990](http://localhost:8990)

---
