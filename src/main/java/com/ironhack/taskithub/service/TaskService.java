package com.ironhack.taskithub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ironhack.taskithub.dto.TaskDTO;
import com.ironhack.taskithub.dto.TaskSummaryDTO;
import com.ironhack.taskithub.model.Department;
import com.ironhack.taskithub.model.Task;
import com.ironhack.taskithub.model.User;
import com.ironhack.taskithub.repository.DepartmentRepository;
import com.ironhack.taskithub.repository.TaskRepository;
import com.ironhack.taskithub.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * TaskService
 */
@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public Task createTaskFromDTO(TaskDTO taskDTO) {
        Task task = taskDTO.toTask();
        return createTask(task, taskDTO.getDepartmentId(), taskDTO.getCreatedById(), taskDTO.getAssignedUserIds());
    }

    public Task createTask(Task task, Long departmentId, Long createdById, List<Long> assignedUserIds) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        User createdBy = userRepository.findById(createdById)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        task.setDepartment(department);
        task.setCreatedBy(createdBy);

        if (assignedUserIds != null) {
            List<User> assignedUsers = assignedUserIds.stream()
                    .map(id -> userRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("User not found")))
                    .collect(Collectors.toList());
            task.setAssignedUsers(assignedUsers);
        }

        return taskRepository.save(task);
    }

    public List<Task> getTasksByDepartment(Long departmentId) {
        return taskRepository.findByDepartmentId(departmentId);
    }

    public List<Task> getTasksCreatedByUser(Long userId) {
        return taskRepository.findByCreatedBy_Id(userId);
    }

    public List<Task> getTasksAssignedToUser(Long userId) {
        return taskRepository.findByAssignedUsers_Id(userId);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task updateTaskFromDTO(Long id, TaskDTO taskDTO) {
        Task task = taskDTO.toTask();
        return updateTask(id, task, taskDTO.getAssignedUserIds());
    }

    public Task updateTask(Long id, Task updatedTask, List<Long> assignedUserIds) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());

        // Find and set the department
        if (updatedTask.getDepartment() != null) {
            Department department = departmentRepository.findById(updatedTask.getDepartment().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found"));
            existingTask.setDepartment(department);
        }

        existingTask.setDueDate(updatedTask.getDueDate());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setStatus(updatedTask.getStatus());

        // Update the "updatedAt" value
        existingTask.setUpdatedAt(LocalDateTime.now());

        if (assignedUserIds != null) {
            List<User> assignedUsers = assignedUserIds.stream()
                    .map(userId -> userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found")))
                    .collect(Collectors.toList());
            existingTask.setAssignedUsers(assignedUsers);
        }

        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public TaskSummaryDTO toTaskSummaryDTO(Task task) {
        TaskSummaryDTO dto = new TaskSummaryDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setDueDate(task.getDueDate());
        dto.setPriority(task.getPriority());
        dto.setStatus(task.getStatus());
        dto.setDepartmentId(task.getDepartment().getId());
        dto.setCreatedById(task.getCreatedBy().getId());
        dto.setAssignedUserIds(task.getAssignedUsers().stream().map(User::getId).collect(Collectors.toList()));
        return dto;
    }

    public TaskDTO toTaskDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setStatus(task.getStatus());
        dto.setDepartmentId(task.getDepartment().getId());
        dto.setCreatedById(task.getCreatedBy().getId());
        return dto;
    }
}
