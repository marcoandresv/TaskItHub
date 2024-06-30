package com.ironhack.taskithub.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ironhack.taskithub.dto.DepartmentDTO;
import com.ironhack.taskithub.dto.DepartmentSummaryDTO;

import com.ironhack.taskithub.model.Department;
import com.ironhack.taskithub.service.DepartmentService;

/**
 * DepartmentController
 */
@RestController
@RequestMapping("/departments")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DepartmentSummaryDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO) {
        Department createdDepartment = departmentService.createDepartmentFromDTO(departmentDTO);
        if (createdDepartment != null) {
            return ResponseEntity.ok(departmentService.toDepartmentSummaryDTO(createdDepartment));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DepartmentSummaryDTO> getDepartmentById(@PathVariable Long id) {
        Department department = departmentService.getDepartmentById(id);
        if (department != null) {
            return ResponseEntity.ok(departmentService.toDepartmentSummaryDTO(department));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        List<DepartmentDTO> departmentsDTOs = departments.stream()
            .map(departmentService::toDepartmentDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(departmentsDTOs);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DepartmentSummaryDTO> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDTO departmentDTO) {
        Department updatedDepartment = departmentService.updateDepartmentFromDTO(id, departmentDTO);
        if (updatedDepartment != null) {
            return ResponseEntity.ok(departmentService.toDepartmentSummaryDTO(updatedDepartment));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
