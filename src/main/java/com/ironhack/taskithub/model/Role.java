package com.ironhack.taskithub.model;

import java.util.ArrayList;
import java.util.List;

//import java.util.HashSet;
//import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Role
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity{
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();
    // NOTE: Investigate how to use Set
    // private Set<User> users = new HashSet<>();

    
}
