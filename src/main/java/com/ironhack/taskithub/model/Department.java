package com.ironhack.taskithub.model;

import java.util.ArrayList;
import java.util.List;
//import java.util.Set;
//import java.util.HashSet;
//NOTE: Investigate how to use Set

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;

/**
 * Department
 */
@Entity
@Data
public class Department extends BaseEntity{
    private String name;

    @OneToMany(mappedBy = "department")
    private List<Task> tasks = new ArrayList<>();
    //private Set<Task> tasks = new HashSet<>();

    @ManyToMany(mappedBy = "departments")
    private List<User> users = new ArrayList<>();
    //private Set<User> users = new HashSet<>();

    
}
