package com.ironhack.taskithub.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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
    private List<Task> tasks = new ArrayList<>();

    @ManyToMany(mappedBy = "departments")
    private List<User> users = new ArrayList<>();

}
