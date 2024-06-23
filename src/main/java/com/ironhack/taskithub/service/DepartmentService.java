package com.ironhack.taskithub.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ironhack.taskithub.model.Department;
import com.ironhack.taskithub.repository.DepartmentRepository;

/**
 * DepartmentService
 */
@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments(){
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public Department updateDepartment(Long id, Department updatedDepartment) {
        Department existingDepartment = departmentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Department not found"));

        // to get and preserve the "createdAt" and "id" values
        updatedDepartment.setCreatedAt(existingDepartment.getCreatedAt());
        updatedDepartment.setId(id);

        // to modify the "updatedAt" value
        updatedDepartment.setUpdatedAt(LocalDateTime.now());

        return departmentRepository.save(updatedDepartment);
    }

    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
    
}
