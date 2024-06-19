package com.ironhack.taskithub.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ironhack.taskithub.enums.Priority;
import com.ironhack.taskithub.enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Task
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Task extends BaseEntity{
    private String title;
    private String description;
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToMany
    private List<User> assignedUsers = new ArrayList<>();

    @ManyToOne
    private Department department;

    @ManyToOne
    private User manager;
    
    @ManyToMany
    private List<User> supervisors = new ArrayList<>();
    
}
