package com.ironhack.taskithub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ironhack.taskithub.model.Department;
import com.ironhack.taskithub.model.User;

/**
 * UserRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    List<User> findByDepartments(List<Department> departments);
    Optional<User> findByUsername(String username);

    
}
