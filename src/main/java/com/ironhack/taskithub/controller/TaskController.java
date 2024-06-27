package com.ironhack.taskithub.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ironhack.taskithub.dto.TaskDTO;
import com.ironhack.taskithub.dto.TaskSummaryDTO;
import com.ironhack.taskithub.model.Task;
import com.ironhack.taskithub.service.TaskService;

/**
 * TaskController
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskSummaryDTO> createTask(@RequestBody TaskDTO taskDTO) {
        Task createdTask = taskService.createTaskFromDTO(taskDTO);
        return ResponseEntity.ok(taskService.toTaskSummaryDTO(createdTask));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<TaskSummaryDTO>> getTasksByDepartment(@PathVariable Long departmentId) {
        List<Task> tasks = taskService.getTasksByDepartment(departmentId);
        return ResponseEntity.ok(tasks.stream().map(taskService::toTaskSummaryDTO).collect(Collectors.toList()));
    }

    @GetMapping("/created-by/{userId}")
    public ResponseEntity<List<TaskSummaryDTO>> getTasksCreatedByUser(@PathVariable Long userId) {
        List<Task> tasks = taskService.getTasksCreatedByUser(userId);
        return ResponseEntity.ok(tasks.stream().map(taskService::toTaskSummaryDTO).collect(Collectors.toList()));
    }

    @GetMapping("/assigned-to/{userId}")
    public ResponseEntity<List<TaskSummaryDTO>> getTasksAssignedToUser(@PathVariable Long userId) {
        List<Task> tasks = taskService.getTasksAssignedToUser(userId);
        return ResponseEntity.ok(tasks.stream().map(taskService::toTaskSummaryDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskSummaryDTO> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(taskService.toTaskSummaryDTO(task));
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        List<TaskDTO> taskDTOs = tasks.stream().map(taskService::toTaskDTO).collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskSummaryDTO> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        Task updatedTask = taskService.updateTaskFromDTO(id, taskDTO);
        return ResponseEntity.ok(taskService.toTaskSummaryDTO(updatedTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
