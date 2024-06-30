package com.ironhack.taskithub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ironhack.taskithub.enums.Role;
import com.ironhack.taskithub.model.Department;
import com.ironhack.taskithub.model.User;



/**
 * UserRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByDepartment(Department department);

    List<User> findByUsername(String username);

    List<User> findByRole(Role role);

    List<User> findByDepartmentId(Long departmentId);

    Boolean existsByUsername(String username);
}
