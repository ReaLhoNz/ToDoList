package com.rybka.todolist.Tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public Task taskCreation(String title, String description, int Priority, boolean isCompleted, Timestamp dueDate, LocalDateTime created_at){
        created_at = LocalDateTime.now();
        Task task = new Task(title,description,Priority,isCompleted,dueDate,created_at);
        return taskRepository.save(task);
    }
}
