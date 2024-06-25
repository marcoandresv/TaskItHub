package com.ironhack.taskithub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    // WARN: rework how the task retrieves info and how it is updated
    public Task updateTask(Long id, Task updatedTask, List<Long> assignedUserIds) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());

        // TODO: find department by department Id, if doesn't exist throw error, if it does, save
        existingTask.setDepartment(updatedTask.getDepartment());

        existingTask.setDueDate(updatedTask.getDueDate());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setStatus(updatedTask.getStatus());

        // to modify the "updatedAt" value
        existingTask.setUpdatedAt(LocalDateTime.now());

        if (assignedUserIds != null) {

            List<User> assignedUsers = userRepository.findAllById(assignedUserIds); // if doesn't find, throw error
            existingTask.setAssignedUsers(assignedUsers);

        }

        //List<User> assignedUsers = userRepository.findAllById(assignedUserIds);
        //existingTask.setAssignedUsers(assignedUsers);
        //
        return taskRepository.save(updatedTask);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
