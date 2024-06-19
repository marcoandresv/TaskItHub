package com.ironhack.taskithub.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    private String name;
    private String username;
    private String password;

    @ManyToMany
    private List<Department> departments = new ArrayList<>();

    @ManyToMany
    private List<Task> tasks = new ArrayList<>();

    @ManyToMany
    private List<Role> roles = new ArrayList<>();

}
