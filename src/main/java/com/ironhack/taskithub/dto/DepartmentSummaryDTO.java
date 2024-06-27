package com.ironhack.taskithub.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * DepartmentSummaryDTO
 */
@Data
public class DepartmentSummaryDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> userIds;
    private List<Long> taskIds;
}
