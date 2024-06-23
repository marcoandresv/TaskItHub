package com.ironhack.taskithub.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Task createTask(Task task, Long departmentId, Long createdById, List<Long> assignedUserIds) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        User createdBy = userRepository.findById(createdById)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<User> assignedUsers = userRepository.findAllById(assignedUserIds);

        task.setDepartment(department);
        task.setCreatedBy(createdBy);
        task.setAssignedUsers(assignedUsers);

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

    public Task updateTask(Long id, Task updatedTask, List<Long> assignedUserIds) {

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        List<User> assignedUsers = userRepository.findAllById(assignedUserIds);


        // to get and preserve the "createdAt" and "id" values
        updatedTask.setCreatedAt(existingTask.getCreatedAt());
        updatedTask.setId(id);

        // to modify the "updatedAt" value
        updatedTask.setUpdatedAt(LocalDateTime.now());

        updatedTask.setAssignedUsers(assignedUsers);

        return taskRepository.save(updatedTask);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
