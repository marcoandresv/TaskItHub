package com.ironhack.taskithub.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ironhack.taskithub.dto.DepartmentDTO;
import com.ironhack.taskithub.dto.DepartmentSummaryDTO;

import com.ironhack.taskithub.model.Department;
import com.ironhack.taskithub.model.Task;
import com.ironhack.taskithub.model.User;
import com.ironhack.taskithub.repository.DepartmentRepository;
import com.ironhack.taskithub.repository.TaskRepository;
import com.ironhack.taskithub.repository.UserRepository;

/**
 * DepartmentService
 */
@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public Department createDepartmentFromDTO(DepartmentDTO departmentDTO) {
        Department department = departmentDTO.toDepartment();
        return createDepartment(department, departmentDTO.getTaskIds(), departmentDTO.getUserIds());
    }

    public Department createDepartment(Department department, List<Long> taskIds, List<Long> userIds) {
        if (userIds != null) {
            List<User> users = userIds.stream()
                .map(userId -> userRepository.findById(userId).orElse(null))
                .filter(user -> user != null)
                .collect(Collectors.toList());
            department.setUsers(users);
        }

        if (taskIds != null) {
            List<Task> tasks = taskIds.stream()
                .map(taskId -> taskRepository.findById(taskId).orElse(null))
                .filter(task -> task != null)
                .collect(Collectors.toList());
            department.setTasks(tasks);
        }

        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    public Department updateDepartmentFromDTO(Long id, DepartmentDTO departmentDTO) {
        Department department = departmentDTO.toDepartment();
        return updateDepartment(id, department, departmentDTO.getTaskIds(), departmentDTO.getUserIds());
    }

    public Department updateDepartment(Long id, Department updatedDepartment, List<Long> taskIds, List<Long> userIds) {
        Department existingDepartment = departmentRepository.findById(id).orElse(null);
        if (existingDepartment == null) {
            return null;
        }

        if (updatedDepartment.getName() != null) {
            existingDepartment.setName(updatedDepartment.getName());
        }

        if (userIds != null) {
            List<User> users = userIds.stream()
                .map(userId -> userRepository.findById(userId).orElse(null))
                .filter(user -> user != null)
                .collect(Collectors.toList());
            existingDepartment.setUsers(users);
        }

        if (taskIds != null) {
            List<Task> tasks = taskIds.stream()
                .map(taskId -> taskRepository.findById(taskId).orElse(null))
                .filter(task -> task != null)
                .collect(Collectors.toList());
            existingDepartment.setTasks(tasks);
        }

        return departmentRepository.save(existingDepartment);
    }

    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    public DepartmentSummaryDTO toDepartmentSummaryDTO(Department department) {
        DepartmentSummaryDTO dto = new DepartmentSummaryDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setCreatedAt(department.getCreatedAt());
        dto.setUpdatedAt(department.getUpdatedAt());
        dto.setUserIds(department.getUsers().stream().map(User::getId).collect(Collectors.toList()));
        dto.setTaskIds(department.getTasks().stream().map(Task::getId).collect(Collectors.toList()));
        return dto;
    }

    public DepartmentDTO toDepartmentDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setCreatedAt(department.getCreatedAt());
        dto.setUserIds(department.getUsers().stream().map(User::getId).collect(Collectors.toList()));
        dto.setTaskIds(department.getTasks().stream().map(Task::getId).collect(Collectors.toList()));
        return dto;
    }
}
