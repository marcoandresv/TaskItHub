package com.ironhack.taskithub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ironhack.taskithub.model.Department;

/**
 * DepartmentRepository
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>{

    
}
