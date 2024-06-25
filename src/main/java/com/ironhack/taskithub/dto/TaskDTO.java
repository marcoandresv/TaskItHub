package com.ironhack.taskithub.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ironhack.taskithub.model.Task;

import com.ironhack.taskithub.enums.*;

import lombok.Data;

/**
 * TaskDTO
 */
@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Priority priority;
    private Status status;
    private Long departmentId;
    private Long createdById;
    private List<Long> assignedUserIds;

    public Task toTask() {
        Task task = new Task();
        task.setId(this.id);
        task.setTitle(this.title);
        task.setDescription(this.description);
        task.setDueDate(this.dueDate);
        task.setPriority(this.priority);
        task.setStatus(this.status);
        return task;
    }
    
}