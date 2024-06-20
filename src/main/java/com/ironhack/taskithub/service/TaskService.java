package com.ironhack.taskithub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ironhack.taskithub.model.Task;
import com.ironhack.taskithub.repository.TaskRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * TaskService
 */
@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getTasksByDepartment(Long departmentId) {
        return taskRepository.findByDepartmentId(departmentId);
    }

    public List<Task> getTasksByUser(Long userId) {
        return taskRepository.findByAssignedUsers_Id(userId);

    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task updateTask(Task task) {
        if (!taskRepository.existsById(task.getId())) {
            throw new EntityNotFoundException("Task not found");
        }
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
