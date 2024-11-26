package com.rybka.todolist.Tasks;

import com.rybka.todolist.ToDoLists.TodoLists;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name ="tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "list_id", nullable = false)
    private TodoLists todoLists;

    private String title;
    private String description;
    private int priority;
    private boolean is_completed;
    private Timestamp due_date;
    private LocalDateTime created_at;

    public Task(String title, String description, int priority, boolean is_completed, Timestamp due_date, LocalDateTime created_at) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.is_completed = is_completed;
        this.due_date = due_date;
        this.created_at = created_at;
    }
    @PrePersist
    protected void onCreate() {
        if (created_at == null) {
            created_at = LocalDateTime.now();
        }
    }
}
