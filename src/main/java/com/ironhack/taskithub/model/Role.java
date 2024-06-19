package com.ironhack.taskithub.model;

import java.util.ArrayList;
import java.util.List;

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
public class Role extends BaseEntity {
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

}
