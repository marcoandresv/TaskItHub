package com.ironhack.taskithub.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Department
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Department extends BaseEntity {
    private String name;

    @OneToMany(mappedBy = "department")
    private List<Task> tasks;

    @OneToMany(mappedBy = "department")
    private List<User> users;

}
