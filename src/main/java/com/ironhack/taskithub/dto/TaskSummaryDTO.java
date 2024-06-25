package com.ironhack.taskithub.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ironhack.taskithub.enums.*;

import lombok.Data;

/**
 * TaskSummaryDTO
 */
@Data
public class TaskSummaryDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
    private Priority priority;
    private Status status;
    private Long departmentId;
    private Long createdById;
    private List<Long> assignedUserIds;

    

}
