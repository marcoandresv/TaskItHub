package com.ironhack.taskithub.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ironhack.taskithub.enums.Role;

import lombok.Data;

/**
 * UserSummaryDTO
 */
@Data
public class UserSummaryDTO {
    public Long id;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public String name;
    public String username;
    public String password;
    public Role role;
    public Long departmentId;
    public List<Long> taskIds;

    
}
