package com.ironhack.taskithub.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * UserDTO
 */
@Data
public class UserDTO {
    private Long id;
    private String name;
    private String username;
    private String password;
    private String role;
    private Long departmentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> taskIds;
}