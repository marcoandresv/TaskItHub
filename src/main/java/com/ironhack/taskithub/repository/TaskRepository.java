package com.ironhack.taskithub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ironhack.taskithub.model.Task;

/**
 * TaskRepository
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
    List<Task> findByDepartmentId(Long departmentId);
    List<Task> findByAssignedUsers_Id(Long userId);
    List<Task> findByManagerId(Long managerId);



    
}
