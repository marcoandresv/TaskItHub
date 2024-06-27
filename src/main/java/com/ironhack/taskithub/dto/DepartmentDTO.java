package com.ironhack.taskithub.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ironhack.taskithub.model.Department;

import lombok.Data;

/**
 * DepartmentDTO
 */
@Data
public class DepartmentDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt; 
    List<Long> taskIds;
    List<Long> userIds;

    public Department toDepartment() {
        Department department = new Department();
        department.setId(id);
        department.setName(name);
        department.setCreatedAt(createdAt);
        return department;
    }


    
}
