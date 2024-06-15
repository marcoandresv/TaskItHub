package com.ironhack.taskithub.model;

import java.util.ArrayList;
import java.util.List;
//import java.util.HashSet;
//import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Data;

/**
 * User
 */
@Entity
@Data
public class User extends BaseEntity {
    private String name;
    private String username;
    private String password;

    @ManyToMany
    private List<Department> departments = new ArrayList<>();
    // NOTE: investigate sets
    // private Set<Department> departments = new HashSet<>();

    @ManyToMany
    private List<Task> tasks = new ArrayList<>();
    // private Set<Task> tasks = new HashSet<>();

    @ManyToMany
    private List<Role> roles = new ArrayList<>();
    // private Set<Role> roles = new HashSet<>();

}
