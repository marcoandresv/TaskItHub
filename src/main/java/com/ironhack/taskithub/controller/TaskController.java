package com.ironhack.taskithub.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        Task task = taskDTO.toTask();
        Long departmentId = taskDTO.getDepartmentId();
        Long createdById = taskDTO.getCreatedById();
        List<Long> assignedUserIds = taskDTO.getAssignedUserIds();

        Task createdTask = taskService.createTask(task, departmentId, createdById, assignedUserIds);
        return ResponseEntity.ok(toTaskSummaryDTO(createdTask));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<TaskSummaryDTO>> getTasksByDepartment(@PathVariable Long departmentId) {
        List<Task> tasks = taskService.getTasksByDepartment(departmentId);
        return ResponseEntity.ok(tasks.stream().map(this::toTaskSummaryDTO).collect(Collectors.toList()));
    }

    @GetMapping("/created-by/{userId}")
    public ResponseEntity<List<TaskSummaryDTO>> getTasksCreatedByUser(@PathVariable Long userId) {
        List<Task> tasks = taskService.getTasksCreatedByUser(userId);
        return ResponseEntity.ok(tasks.stream().map(this::toTaskSummaryDTO).collect(Collectors.toList()));
    }

    @GetMapping("/assigned-to/{userId}")
    public ResponseEntity<List<TaskSummaryDTO>> getTasksAssignedToUser(@PathVariable Long userId) {
        List<Task> tasks = taskService.getTasksAssignedToUser(userId);
        return ResponseEntity.ok(tasks.stream().map(this::toTaskSummaryDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskSummaryDTO> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(toTaskSummaryDTO(task));
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        List<TaskDTO> taskDTOs = tasks.stream().map(task -> {
            TaskDTO taskDTO = new TaskDTO();
            taskDTO.setId(task.getId());
            taskDTO.setTitle(task.getTitle());
            taskDTO.setDescription(task.getDescription());
            taskDTO.setStatus(task.getStatus());
            taskDTO.setDepartmentId(task.getDepartment().getId());
            taskDTO.setCreatedById(task.getCreatedBy().getId());
            return taskDTO;
        }).toList();
        return ResponseEntity.ok(taskDTOs);
    }

    // WARN: rework how the task retrieves info and how it is updated
    @PutMapping("/{id}")
    public ResponseEntity<TaskSummaryDTO> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        Task task = taskDTO.toTask();
        List<Long> assignedUserIds = null;
        if (taskDTO.getAssignedUserIds() != null) {
            //taskDTO.setAssignedUserIds(List.of());
            assignedUserIds = taskDTO.getAssignedUserIds();
        }

        Task updatedTask = taskService.updateTask(id, task, assignedUserIds);
        return ResponseEntity.ok(toTaskSummaryDTO(updatedTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    private TaskSummaryDTO toTaskSummaryDTO(Task task) {
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
        dto.setAssignedUserIds(task.getAssignedUsers().stream().map(user -> user.getId()).collect(Collectors.toList()));
        return dto;
    }
}
