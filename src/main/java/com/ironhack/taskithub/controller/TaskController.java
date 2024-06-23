package com.ironhack.taskithub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ironhack.taskithub.dto.TaskDTO;
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
    public ResponseEntity<Task> createTask(@RequestBody TaskDTO taskDTO) {
        Task task = taskDTO.getTask();
        Long departmentId = taskDTO.getDepartmentId();
        Long createdById = taskDTO.getCreatedById();
        List<Long> assignedUserIds = taskDTO.getAssignedUserIds();

        return ResponseEntity.ok(taskService.createTask(task, departmentId, createdById, assignedUserIds));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Task>> getTasksByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(taskService.getTasksByDepartment(departmentId));
    }


    @GetMapping("/created-by/{userId}")
    public ResponseEntity<List<Task>> getTasksCreatedByUser(@PathVariable Long userId) {
        List<Task> tasks = taskService.getTasksCreatedByUser(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assigned-to/{userId}")
    public ResponseEntity<List<Task>> getTasksAssignedToUser(@PathVariable Long userId) {
        List<Task> tasks = taskService.getTasksAssignedToUser(userId);
        return ResponseEntity.ok(tasks);
    }



    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        Task task = taskDTO.getTask();
        List<Long> assignedUserIds = taskDTO.getAssignedUserIds();

        Task updatedTask = taskService.updateTask(id, task, assignedUserIds);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
