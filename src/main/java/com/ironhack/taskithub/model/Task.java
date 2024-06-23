package com.ironhack.taskithub.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ironhack.taskithub.enums.Priority;
import com.ironhack.taskithub.enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
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
public class Task extends BaseEntity {
    private String title;
    private String description;
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ManyToMany
    @JoinTable(
        name = "task_assigned_users", 
        joinColumns = @JoinColumn(name = "task_id"), 
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> assignedUsers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    private User manager;

    @ManyToMany
    private List<User> supervisors = new ArrayList<>();

}
