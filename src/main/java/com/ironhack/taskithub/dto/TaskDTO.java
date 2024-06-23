package com.ironhack.taskithub.dto;

import java.util.List;

import com.ironhack.taskithub.model.Task;

import lombok.Data;

/**
 * TaskDTO
 */
@Data
public class TaskDTO {
    private Task task;
    private Long departmentId;
    private Long createdById;
    private List<Long> assignedUserIds;


    
}
